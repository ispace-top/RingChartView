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

sealed class ChartAngleStyle(val angle: Float) {
    object HalfCircle : ChartAngleStyle(180f)
    object FullCircle : ChartAngleStyle(360f)
}

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

        val radius = (min(width, height) - strokeWidthPx) / 2f
        val rectF = if (chartAngleStyle is ChartAngleStyle.HalfCircle) {
            RectF(
                (width / 2f - radius),
                (height / 2f - radius * 0.5f),
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

        // 背景弧
        drawArc(
            color = backColor,
            startAngle = drawStartAngle + if (paintCap == StrokeCap.Round) 0.5f * (360f / (2 * Math.PI.toFloat() * radius)) * strokeWidthPx else 0f,
            sweepAngle = currentSweepAngle * animatedPhase.value - if (paintCap == StrokeCap.Round) (360f / (2 * Math.PI.toFloat() * radius)) * strokeWidthPx else 0f,
            useCenter = false,
            style = Stroke(width = strokeWidthPx, cap = paintCap)
        )

        // 进度弧
        if (isMultiProgress) {
            var currentStartAngle = drawStartAngle
            val processedNodes = if (protectMinValue) {
                val totalValue = progressNodes.sumOf { it.value.toDouble() }.toFloat()
                progressNodes.map { node ->
                    if (node.value / totalValue < minProgress / currentSweepAngle) {
                        val temp = minProgress * totalValue / currentSweepAngle
                        node.copy(value = temp)
                    } else {
                        node
                    }
                }
            } else {
                progressNodes
            }

            processedNodes.forEach { node ->
                val sweep = (currentSweepAngle / maxValue * node.value)
                var resultSweep = sweep * animatedPhase.value

                if (protectMinValue && resultSweep < minProgress && resultSweep != 0f) {
                    resultSweep = minProgress
                } else if (resultSweep == 0f && node.value > 0) {
                    resultSweep = 0.1f // 确保即使是极小值也能显示
                }

                drawArc(
                    color = node.color,
                    startAngle = currentStartAngle,
                    sweepAngle = resultSweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidthPx, cap = paintCap)
                )
                currentStartAngle += sweep * animatedPhase.value
            }
        } else {
            var sweep = currentSweepAngle / maxValue * progress
            if (protectMinValue && sweep < minProgress && sweep != 0f) {
                sweep = minProgress
            } else if (sweep == 0f && progress > 0) {
                sweep = 0.1f // 确保即使是极小值也能显示
            }

            drawArc(
                color = progressColor,
                startAngle = drawStartAngle + if (paintCap == StrokeCap.Round) 0.5f * (360f / (2 * Math.PI.toFloat() * radius)) * strokeWidthPx else 0f,
                sweepAngle = sweep * animatedPhase.value - if (paintCap == StrokeCap.Round) (360f / (2 * Math.PI.toFloat() * radius)) * strokeWidthPx else 0f,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = paintCap)
            )
        }
    }
}

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
        maxValue = 100,
        isMultiProgress = true,
        progressNodes = nodes,
        chartAngleStyle = ChartAngleStyle.HalfCircle,
        paintWidth = 15.dp,
        backColor = androidx.compose.ui.graphics.Color(0xFFFF9800) // 与 MainActivity 保持一致
    )
}
