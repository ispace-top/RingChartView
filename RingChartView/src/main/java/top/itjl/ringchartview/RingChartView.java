package top.itjl.ringchartview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Kerwin
 * @date: 2021/9/30
 */
public class RingChartView extends View {
    private static final String TAG = "RingChartView";
    //图表是半圆型
    public static final float HALF_CIRCLE = 180;
    //图标为全圆
    public static final float FULL_CIRCLE = 360;
    //图标开始点，即 0点值位置
    public static final float START_RIGHT = 0, START_BOTTOM = 90, START_LEFT = 180, START_TOP = 270;

    private int animationTime = 1500;//动画持续时间
    private float drawStart = START_LEFT;
    private float chartAngleStyle = HALF_CIRCLE;
    private int maxValue = 100;
    private float chartSweepAngle = HALF_CIRCLE;
    private int progress = 0;
    private int backGroundColor = Color.LTGRAY;
    private int progressColor = Color.GREEN;
    private boolean isMutilProgress = true;
    private float paintWidth = 40;
    private Paint mPaint = new Paint();

    private List<ProgressNode> progressNodes = new ArrayList<>();

    private final SemiAnimator semiAnimator = new SemiAnimator(animation -> postInvalidate());
    private RectF rectF;
    private float phaseS;
    private int width;
    private int height;
    private boolean protectMinValue = true;
    private float minProgress = 1;
    private int paintCap = 1;

    public RingChartView(Context context) {
        this(context, null);
    }

    public RingChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RingChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs);
    }


    private void initialize(Context context, AttributeSet attrs) {
        getXMLAttrs(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(paintWidth);
        mPaint.setAntiAlias(true); // 抗锯齿
        mPaint.setDither(true); // 防抖动
        mPaint.setStyle(Paint.Style.STROKE); // 设置绘制的圆为空心
    }

    private void getXMLAttrs(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RingChartView);
        // 获取自定义属性和默认值
        backGroundColor = mTypedArray.getColor(R.styleable.RingChartView_backColor, Color.LTGRAY);
        isMutilProgress = mTypedArray.getBoolean(R.styleable.RingChartView_mutilProgress, true);

        protectMinValue = mTypedArray.getBoolean(R.styleable.RingChartView_protectMinValue, true);
        minProgress = mTypedArray.getFloat(R.styleable.RingChartView_minProgress, 1);

        chartAngleStyle = mTypedArray.getFloat(R.styleable.RingChartView_chartAngleStyle, HALF_CIRCLE);
        drawStart = mTypedArray.getFloat(R.styleable.RingChartView_drawStart, START_LEFT);

        maxValue = mTypedArray.getInteger(R.styleable.RingChartView_maxValue, 100);
        progressColor = mTypedArray.getColor(R.styleable.RingChartView_progressColor, Color.GREEN);
        progress = mTypedArray.getInteger(R.styleable.RingChartView_progress, 0);
        paintWidth = mTypedArray.getDimension(R.styleable.RingChartView_paintWidth, 30);

        paintCap = mTypedArray.getInt(R.styleable.RingChartView_paintCap, 1);

        animationTime = mTypedArray.getInteger(R.styleable.RingChartView_animationTime, 1500);
        mTypedArray.recycle();
        float offsetAngle = chartAngleStyle == FULL_CIRCLE ? 0 : chartAngleStyle - drawStart;
        chartSweepAngle = chartAngleStyle == FULL_CIRCLE ? 360 : chartSweepAngle + offsetAngle * 2;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        semiAnimator.release();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        semiAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            int finalWidth = (int) (paintWidth * 5);
            int finalHeight = chartSweepAngle == HALF_CIRCLE ? (int) (finalWidth / 2 + paintWidth) : finalWidth;
            setMeasuredDimension(finalWidth, finalHeight);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            int finalWidth = (int) ((chartSweepAngle == HALF_CIRCLE ? height * 2 : height) - paintWidth);
            finalWidth = Math.min(finalWidth, width);
            setMeasuredDimension(finalWidth, height);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            int finalHeight = chartSweepAngle == HALF_CIRCLE ? (int) (width / 2 + paintWidth) : width;
            setMeasuredDimension(width, finalHeight);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        phaseS = semiAnimator.getPhaseS();
        //扇形半径， 组件宽度的一半减去padding再减去画笔的宽度；
        float radius = (width >> 1) - paintWidth;
        rectF = new RectF(
                (width >> 1) - radius + getPaddingLeft(),
                (height >> 1) - (chartAngleStyle == HALF_CIRCLE ? radius * 0.5f : radius) + getPaddingTop(),
                (width >> 1) + radius - getPaddingRight(),
                (height >> 1) + (chartAngleStyle == HALF_CIRCLE ? radius * 1.5f : radius) - getPaddingBottom());
        /*
         * 图层叠加去交集操作逻辑
         * */
        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        drawDst(canvas);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        drawSrc(canvas);
        mPaint.setXfermode(null);
        canvas.restoreToCount(sc);
    }

    private void drawSrc(Canvas canvas) {
        if (isMutilProgress) {
            drawMutilProgress(canvas, phaseS);
        } else {
            drawSignleProgress(canvas, phaseS);
        }
    }

    private void drawDst(Canvas canvas) {
        mPaint.setStrokeCap(paintCap == 1 ? Paint.Cap.ROUND : Paint.Cap.SQUARE);
        mPaint.setColor(backGroundColor);
        canvas.drawArc(rectF, drawStart, chartSweepAngle * phaseS, false, mPaint);
    }

    //绘制单一进度
    private void drawSignleProgress(Canvas canvas, float phaseS) {
        float sweep = chartSweepAngle / maxValue * progress;//扫过角度
        if (sweep > drawStart + chartSweepAngle) {
            sweep = drawStart + chartSweepAngle;
        }
        if (protectMinValue && sweep < minProgress) sweep = minProgress;
        mPaint.setColor(progressColor);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawArc(rectF, drawStart, sweep * phaseS, false, mPaint);
    }

    /**
     * 绘制多层进度
     *
     * @param canvas 画布
     */
    @SuppressLint("LongLogTag")
    private void drawMutilProgress(Canvas canvas, float phaseS) {
        float begin = drawStart;
        mPaint.setStrokeCap(Paint.Cap.SQUARE); // 把每段圆弧改成直角的

        for (ProgressNode node : progressNodes) {
            if (node == null) return;
            float sweep = chartSweepAngle / maxValue * node.value;//扫过角度
            if (protectMinValue && sweep < minProgress) sweep = minProgress;
            if (begin > drawStart + chartSweepAngle) return;
            if (begin + sweep > drawStart + chartSweepAngle) {
                sweep = drawStart + chartSweepAngle - begin;
            }
            mPaint.setColor(node.color);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawArc(rectF, begin, sweep * phaseS, false, mPaint);
            begin += sweep * phaseS;
        }
    }


    /**
     * 获取动画时长
     *
     * @return 毫秒
     */
    public int getAnimationTime() {
        return animationTime;
    }

    /**
     * 设置动画时长
     *
     * @param animationTime 时长 毫秒
     */
    public void setAnimationTime(int animationTime) {
        this.animationTime = animationTime;
    }

    /**
     * 获取开始绘制的点位的角度
     *
     * @return 角度
     */
    public float getDrawStart() {
        return drawStart;
    }

    /**
     * 设置开始绘制的点位角度
     *
     * @param drawStart 角度
     */
    public void setDrawStart(float drawStart) {
        this.drawStart = drawStart;
    }

    /**
     * 获取图表样式
     *
     * @return {@value HALF_CIRCLE,FULL_CIRCLE}
     */
    public float getChartAngleStyle() {
        return chartAngleStyle;
    }

    /**
     * 设置图表样式
     *
     * @param chartAngleStyle {@value HALF_CIRCLE,FULL_CIRCLE}
     */
    public void setChartAngleStyle(float chartAngleStyle) {
        this.chartAngleStyle = chartAngleStyle;
    }

    /**
     * 获取最大值
     *
     * @return maxValue
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * 设置最大值
     *
     * @param maxValue maxValue
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        invalidate();
    }

    public float getChartSweepAngle() {
        return chartSweepAngle;
    }

    public void setChartSweepAngle(float chartSweepAngle) {
        this.chartSweepAngle = chartSweepAngle;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public int getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(int backGroundColor) {
        this.backGroundColor = backGroundColor;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public boolean isMutilProgress() {
        return isMutilProgress;
    }

    public void setMutilProgress(boolean mutilProgress) {
        isMutilProgress = mutilProgress;
    }

    public float getPaintWidth() {
        return paintWidth;
    }

    public void setPaintWidth(float paintWidth) {
        this.paintWidth = paintWidth;
    }

    public boolean isProtectMinValue() {
        return protectMinValue;
    }

    public void setProtectMinValue(boolean protectMinValue) {
        this.protectMinValue = protectMinValue;
    }

    public float getMinProgress() {
        return minProgress;
    }

    public void setMinProgress(float minProgress) {
        this.minProgress = minProgress;
    }

    /**
     * 设置多个元素节点
     *
     * @param progressNodes 元素节点列表
     */
    public void setProgressNodes(List<ProgressNode> progressNodes) {
        this.progressNodes = progressNodes;
        semiAnimator.start();
        invalidate();
    }


    /**
     * 播放动画
     */
    public void playAnimation() {
        semiAnimator.start();
        invalidate();
    }

    /**
     * 直线绘制持续的动画类
     */
    private class SemiAnimator {

        private float mPhaseS = 1f; //默认动画值0f-1f
        private final ValueAnimator.AnimatorUpdateListener mListener;//监听
        private ObjectAnimator objectAnimator;

        private SemiAnimator(ValueAnimator.AnimatorUpdateListener listener) {
            mListener = listener;
        }

        private float getPhaseS() {
            return mPhaseS;
        }

        private void setPhaseS(float phase) {
            mPhaseS = phase;
        }

        /**
         * Y轴动画
         */
        private void start() {
            release();
            objectAnimator = ObjectAnimator.ofFloat(this, "phaseS", 0f, 1f);
            objectAnimator.setDuration(animationTime);
            objectAnimator.addUpdateListener(mListener);
            objectAnimator.start();
        }

        /**
         * 释放动画
         */
        private void release() {
            if (objectAnimator != null) {
                objectAnimator.end();
                objectAnimator.cancel();
                objectAnimator = null;
            }
        }
    }

    public static class ProgressNode {
        float value;
        int color;

        public ProgressNode(float value, int color) {
            this.value = value;
            this.color = color;
        }
    }
}
