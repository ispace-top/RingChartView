package top.itjl.ringchart.component

import android.graphics.RectF
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.itjl.ringchart.model.ProgressNode
import kotlin.math.min

// 默认值
private const val DEFAULT_MAX_VALUE = 100
private const val DEFAULT_PROGRESS = 0
private const val DEFAULT_MIN_PROGRESS = 1f
private const val DEFAULT_PAINT_WIDTH_DP = 40f
private const val DEFAULT_ANIMATION_TIME = 1200

/**
 * 定义环形图绘制角度样式的密封类。
 * @param angle 对应的角度值，例如180f表示半圆，360f表示整圆。
 */
sealed class ChartAngleStyle(val angle: Float) {
    object HalfCircle : ChartAngleStyle(180f)
    object FullCircle : ChartAngleStyle(360f)
}

/**
 * 环形图表组件。
 * 支持单色进度条和多段进度条显示。
 *
 * @param modifier Composable修饰符。
 * @param maxValue 图表的最大刻度值。
 * @param progress 当前进度值（单色模式下有效）。
 * @param minProgress 可视化的最小进度颗粒度，确保即使极小值也能显示。
 * @param paintCap 线段端头风格，例如圆头(StrokeCap.Round)或平头(StrokeCap.Square)。
 * @param paintWidth 线段粗细（单位：dp）。
 * @param backColor 背景环的颜色。
 * @param progressColor 前景色（单色进度条颜色）。
 * @param chartAngleStyle 图表角度样式，半圆(HalfCircle)或整圆(FullCircle)。
 * @param drawStartAngle 绘制起始角度（默认180°水平起始，即从左侧中心开始）。
 * @param isMultiProgress 是否开启多段式进度条模式。
 * @param progressNodes 多段进度条数据列表（当isMultiProgress为true时有效）。
 * @param animationTime 动画持续时间（毫秒）。
 * @param protectMinValue 是否保护最小值，确保即使极小值也能显示。
 */
@Composable
fun RingChartView(
    modifier: Modifier = Modifier,
    maxValue: Int = DEFAULT_MAX_VALUE,
    progress: Int = DEFAULT_PROGRESS,
    minProgress: Float = DEFAULT_MIN_PROGRESS,
    paintCap: StrokeCap = StrokeCap.Round,
    paintWidth: Dp = DEFAULT_PAINT_WIDTH_DP.dp,
    backColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.LightGray,
    progressColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Green,
    chartAngleStyle: ChartAngleStyle = ChartAngleStyle.HalfCircle,
    drawStartAngle: Float = 180f, // 对应原生的 START_LEFT
    isMultiProgress: Boolean = false,
    progressNodes: List<ProgressNode> = emptyList(),
    animationTime: Int = DEFAULT_ANIMATION_TIME,
    protectMinValue: Boolean = true
) {
    val animatedPhase = remember { Animatable(0f) }

    // 监听数据变化，触发动画
    LaunchedEffect(maxValue, progress, progressNodes) {
        animatedPhase.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animationTime, easing = LinearEasing)
        )
    }

    val strokeWidthPx = with(LocalDensity.current) { paintWidth.toPx() }

    Canvas(modifier = modifier.padding((strokeWidthPx / 2f / LocalDensity.current.density).dp)) {
        val width = size.width
        val height = size.height

        // 计算绘制圆弧的半径
        val radius = (min(width, height) - strokeWidthPx) / 2f
        // 根据图表样式确定绘制区域的RectF
        val rectF = if (chartAngleStyle is ChartAngleStyle.HalfCircle) {
            RectF(
                (width / 2f - radius),
                (height / 2f - radius * 0.5f), // 调整Y轴，使其在半圆模式下居中
                (width / 2f + radius),
                (height / 2f + radius * 1.5f)
            )
        } else {
            RectF(
                (width / 2f - radius),
                (height / 2f - radius),
                (width / 2f + radius),
                (height / 2f + radius)
            )
        }

        val currentSweepAngle = chartAngleStyle.angle

        // 绘制背景弧
        // 对于圆头端点，需要调整起始和扫描角度以避免圆头超出设定的角度范围
        val capAdjustment = if (paintCap == StrokeCap.Round) {
            // 计算一个圆头大约占据的角度
            // 弧长 = 2 * PI * R * (角度 / 360)
            // 圆头直径 = strokeWidthPx
            // 假设圆头是半圆，其弧长约为 PI * (strokeWidthPx / 2)
            // 那么圆头所占角度 = (PI * strokeWidthPx / 2) / (2 * PI * radius) * 360
            // 简化后大约是 (strokeWidthPx / (4 * radius)) * 360
            (360f / (2 * Math.PI.toFloat() * radius)) * strokeWidthPx * 0.5f
        } else {
            0f
        }

        drawArc(
            color = backColor,
            startAngle = drawStartAngle + capAdjustment,
            sweepAngle = currentSweepAngle * animatedPhase.value - (capAdjustment * 2),
            useCenter = false, // 不连接中心，只绘制弧线
            topLeft = androidx.compose.ui.geometry.Offset(rectF.left, rectF.top),
            size = androidx.compose.ui.geometry.Size(rectF.width(), rectF.height()),
            style = Stroke(width = strokeWidthPx, cap = paintCap)
        )

        // 绘制进度弧
        if (isMultiProgress) {
            var currentStartAngle = drawStartAngle
            val processedNodes = if (protectMinValue) {
                val totalValue = progressNodes.sumOf { it.value.toDouble() }.toFloat()
                if (totalValue == 0f) { // 如果总值为0，则没有需要保护的最小值，直接返回空列表
                    emptyList()
                } else {
                    progressNodes.map { node ->
                        // 保护最小值：如果某个节点的值过小，使其至少达到minProgress所占的角度
                        // 这里我们将其转换为比例，并确保最小角度，再转换回value
                        val requiredMinAngle = minProgress // minProgress代表最小角度
                        val currentNodeSweepAngle = (currentSweepAngle / maxValue * node.value)
                        if (node.value > 0 && currentNodeSweepAngle < requiredMinAngle) {
                            // 计算需要多大的值才能达到minProgress的角度
                            val valueToAchieveMinAngle = (requiredMinAngle * maxValue) / currentSweepAngle
                            node.copy(value = valueToAchieveMinAngle)
                        } else {
                            node
                        }
                    }
                }
            } else {
                progressNodes
            }

            processedNodes.forEach { node ->
                val sweep = (currentSweepAngle / maxValue * node.value) // 计算当前节点的扫描角度
                var resultSweep = sweep * animatedPhase.value

                // 再次检查并确保动画过程中即使是极小值也能显示
                if (protectMinValue && resultSweep > 0f && resultSweep < minProgress) {
                    resultSweep = minProgress // 确保即使动画值很小也能显示
                } else if (resultSweep == 0f && node.value > 0) {
                    // 对于初始值为0但实际值大于0的节点，在动画开始时给一个微小的显示
                    resultSweep = 0.1f
                }

                drawArc(
                    color = node.color,
                    startAngle = currentStartAngle,
                    sweepAngle = resultSweep,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(rectF.left, rectF.top),
                    size = androidx.compose.ui.geometry.Size(rectF.width(), rectF.height()),
                    style = Stroke(width = strokeWidthPx, cap = paintCap)
                )
                // 更新下一个扇区的起始角度
                currentStartAngle += resultSweep // 累加的是实际绘制的扫描角度
            }
        } else {
            // 单色进度条绘制
            var sweep = currentSweepAngle / maxValue * progress
            // 保护最小值
            if (protectMinValue && sweep > 0f && sweep < minProgress) {
                sweep = minProgress
            } else if (sweep == 0f && progress > 0) {
                sweep = 0.1f
            }

            drawArc(
                color = progressColor,
                startAngle = drawStartAngle + capAdjustment,
                sweepAngle = sweep * animatedPhase.value - (capAdjustment * 2),
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(rectF.left, rectF.top),
                size = androidx.compose.ui.geometry.Size(rectF.width(), rectF.height()),
                style = Stroke(width = strokeWidthPx, cap = paintCap)
            )
        }
    }
}

// 预览函数保持不变
@Preview(showBackground = true)
@Composable
fun PreviewRingChartViewFull() {
    RingChartView(
        modifier = Modifier.fillMaxSize(),
        progress = 70,
        paintCap = StrokeCap.Round,
        chartAngleStyle = ChartAngleStyle.FullCircle,
        paintWidth = 15.dp,
        progressColor = androidx.compose.ui.graphics.Color(0xFF8BC34A), // 与 MainActivity 保持一致
        backColor = androidx.compose.ui.graphics.Color(0xFFFF9800) // 与 MainActivity 保持一致
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewRingChartViewHalf() {
    RingChartView(
        modifier = Modifier.fillMaxSize(),
        maxValue = 100,
        progress = 30,
        paintCap = StrokeCap.Square,
        chartAngleStyle = ChartAngleStyle.HalfCircle,
        paintWidth = 15.dp,
        progressColor = androidx.compose.ui.graphics.Color(0xFF8BC34A), // 与 MainActivity 保持一致
        backColor = androidx.compose.ui.graphics.Color(0xFFFF9800) // 与 MainActivity 保持一致
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewRingChartViewMulti() {
    val nodes = listOf(
        ProgressNode(10f, androidx.compose.ui.graphics.Color(0xFF4CAF50)), // chart_green
        ProgressNode(20f, androidx.compose.ui.graphics.Color(0xFF2196F3)), // chart_blue
        ProgressNode(50f, androidx.compose.ui.graphics.Color(0xFFF44336)), // chart_red
        ProgressNode(10f, androidx.compose.ui.graphics.Color(0xFFFFEB3B))  // chart_yellow
    )
    RingChartView(
        modifier = Modifier.fillMaxSize(),
        maxValue = nodes.sumOf { it.value.toDouble() }.toInt(), // 重新计算maxValue
        isMultiProgress = true,
        progressNodes = nodes,
        chartAngleStyle = ChartAngleStyle.HalfCircle,
        paintWidth = 15.dp,
        backColor = androidx.compose.ui.graphics.Color(0xFFFF9800) // 与 MainActivity 保持一致
    )
}
