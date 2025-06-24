package top.itjl.ringchart.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.itjl.ringchart.model.BarChartData
import top.itjl.ringchart.model.TextOrientation
import kotlin.math.max

/**
 * 条形图组件。
 *
 * @param modifier Composable修饰符。
 * @param data 条形图数据。
 * @param animationTime 加速时间（毫秒）。
 * @param showGridLines 是否显示网格线。
 * @param axisColor 坐标轴和网格线的颜色。
 * @param labelTextStyle 坐标轴标签的文本样式。
 */
@Composable
fun BarChartView(
    modifier: Modifier = Modifier,
    data: BarChartData,
    animationTime: Int = 1200,
    showGridLines: Boolean = true,
    axisColor: Color = Color.Gray,
    labelTextStyle: TextStyle = TextStyle(fontSize = 10.sp, color = Color.Black)
) {
    val animatedPhase = remember { Animatable(0f) }
    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(data) {
        animatedPhase.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animationTime, easing = LinearEasing)
        )
    }

    val maxYValue = data.values.maxOrNull() ?: 1f
    val actualMaxY = if (maxYValue == 0f) 1f else maxYValue

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val chartPadding = 32.dp // 图表外边距
        val axisLabelPadding = 8.dp // 轴标签与轴线之间的间距
        val titleLabelPadding = 16.dp // 轴标题与标签之间的间距

        val localDensity = LocalDensity.current
        val chartPaddingPx = with(localDensity) { chartPadding.toPx() }
        val axisLabelPaddingPx = with(localDensity) { axisLabelPadding.toPx() }
        val titleLabelPaddingPx = with(localDensity) { titleLabelPadding.toPx() }

        // 预测量 Y 轴的 0 和最大值文本，用于计算布局空间
        val zeroYTextLayoutResult = textMeasurer.measure("0", labelTextStyle)
        val maxYTextLayoutResult = textMeasurer.measure(actualMaxY.toInt().toString(), labelTextStyle)

        // Y轴数值标签始终横向显示，因此其占据的水平空间是其最大宽度
        val yAxisLabelsMaxWidthPx = with(localDensity) {
            max(zeroYTextLayoutResult.size.width.toFloat(), maxYTextLayoutResult.size.width.toFloat())
        }

        // 预估Y轴标题所需水平空间 (Y轴标题的实际水平尺寸)
        val yAxisTitleEffectiveWidthPx = data.yAxisLabel?.let {
            val titleLayout = textMeasurer.measure(it, labelTextStyle)
            if (data.yAxisLabelOrientation == TextOrientation.Vertical) {
                // 竖向标题，占据的水平空间是其原始高度
                with(localDensity) { titleLayout.size.height.toFloat() }
            } else {
                // 横向标题，占据的水平空间是其原始宽度
                with(localDensity) { titleLayout.size.width.toFloat() }
            }
        } ?: 0f

        // Y轴总共预留的水平空间 (左侧的 Chart Padding + Y轴标题空间 + 标题与标签间距 + Y轴数值标签宽度 + 标签与轴线间距)
        val yAxisTotalReservedHorizontalSpacePx = chartPaddingPx +
                yAxisTitleEffectiveWidthPx +
                (if (yAxisTitleEffectiveWidthPx > 0f) titleLabelPaddingPx else 0f) + // 只有标题存在才加标题到标签的padding
                yAxisLabelsMaxWidthPx +
                axisLabelPaddingPx

        val xAxisLabelHeightPx = with(localDensity) { textMeasurer.measure(data.labels.firstOrNull() ?: "", labelTextStyle).size.height.toFloat() }
        val xAxisTitleHeightPx = data.xAxisLabel?.let { with(localDensity) { textMeasurer.measure(it, labelTextStyle).size.height.toFloat() } } ?: 0f

        // 调整图表绘制区域的边界
        val plotLeft = yAxisTotalReservedHorizontalSpacePx // plotLeft 即为 Y 轴的 X 坐标
        val plotTop = chartPaddingPx
        val plotRight = constraints.maxWidth.toFloat() - chartPaddingPx
        val plotBottom = constraints.maxHeight.toFloat() - chartPaddingPx - xAxisLabelHeightPx - titleLabelPaddingPx - xAxisTitleHeightPx

        val plotWidth = plotRight - plotLeft
        val plotHeight = plotBottom - plotTop

        val barWidthPx = with(localDensity) { data.barWidth.toPx() }
        val spacing = if (data.labels.size > 1) {
            (plotWidth - data.labels.size * barWidthPx) / (data.labels.size - 1).toFloat()
        } else {
            0f
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            // 绘制X轴和Y轴
            drawLine(axisColor, start = Offset(plotLeft, plotBottom), end = Offset(plotRight, plotBottom), strokeWidth = 2f)
            drawLine(axisColor, start = Offset(plotLeft, plotTop), end = Offset(plotLeft, plotBottom), strokeWidth = 2f)

            // 绘制网格线 (可选)
            if (showGridLines) {
                val yStep = plotHeight / 5f
                for (i in 0..5) {
                    val y = plotBottom - i.toFloat() * yStep
                    drawLine(axisColor.copy(alpha = 0.3f), start = Offset(plotLeft, y), end = Offset(plotRight, y), strokeWidth = 1f)
                }
            }

            // 绘制条形柱
            data.values.forEachIndexed { index, value ->
                val xCenter = plotLeft + (barWidthPx / 2f) + index.toFloat() * (barWidthPx + spacing)
                val barHeight = (value / actualMaxY) * plotHeight * animatedPhase.value
                val top = plotBottom - barHeight
                val left = xCenter - barWidthPx / 2f
                val right = xCenter + barWidthPx / 2f
                val bottom = plotBottom

                drawRect(
                    color = data.colors[index % data.colors.size],
                    topLeft = Offset(left, top),
                    size = Size(right - left, bottom - top)
                )

                // 绘制X轴标签
                val label = data.labels[index]
                val textLayoutResult = textMeasurer.measure(label, labelTextStyle)
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(xCenter - textLayoutResult.size.width / 2, plotBottom + axisLabelPaddingPx),
                    color = labelTextStyle.color ?: axisColor
                )
            }

            // 绘制Y轴刻度标签 (0, 最大值及中间刻度) - 始终横向
            val yStepCount = 5
            for (i in 0..yStepCount) {
                val value = (actualMaxY / yStepCount * i).toInt().toString()
                val textLayout = textMeasurer.measure(value, labelTextStyle)
                val y = plotBottom - (plotHeight / yStepCount.toFloat()) * i.toFloat()

                // 横向显示：文本右边缘对齐 Y 轴左侧预留空间的右边缘，垂直居中对齐刻度
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(
                        plotLeft - axisLabelPaddingPx - textLayout.size.width,
                        y - textLayout.size.height / 2f
                    ),
                    color = labelTextStyle.color ?: axisColor
                )
            }

            // 绘制X轴标题
            data.xAxisLabel?.let {
                val measuredText = textMeasurer.measure(it, labelTextStyle)
                drawText(
                    textLayoutResult = measuredText,
                    topLeft = Offset(
                        plotLeft + plotWidth / 2f - measuredText.size.width / 2f,
                        plotBottom + xAxisLabelHeightPx + titleLabelPaddingPx
                    ),
                    color = labelTextStyle.color ?: axisColor
                )
            }

            // 绘制Y轴标题
            data.yAxisLabel?.let {
                val measuredText = textMeasurer.measure(it, labelTextStyle)
                if (data.yAxisLabelOrientation == TextOrientation.Vertical) {
                    // 竖向标题：居中于 Y 轴绘制区域，并根据预留空间定位
                    withTransform({
                        // 旋转原点 X 坐标: chartPaddingPx (图表左边距) + 标题本身高度的一半 (旋转后标题的宽度)
                        // 旋转原点 Y 坐标: 垂直居中于 plotArea
                        translate(
                            left = measuredText.size.width / 2f, // measuredText.size.height是原始文本高度，旋转后是横向占据的空间
                            top = -plotHeight // Y轴标题中心Y
                        )
                        rotate(270f) // 逆时针旋转 90 度
                    }) {
                        // 绘制文本使其中心位于旋转原点
                        drawText(
                            textLayoutResult = measuredText,
                            topLeft = Offset(-measuredText.size.width / 2f, -measuredText.size.height / 2f),
                            color = labelTextStyle.color ?: axisColor
                        )
                    }
                } else {
                    // 横向标题：居中于 Y 轴绘制区域左侧
                    drawText(
                        textLayoutResult = measuredText,
                        topLeft = Offset(
                            chartPaddingPx + yAxisTitleEffectiveWidthPx / 2f - measuredText.size.width / 2f, // Y轴标题中心X
                            plotTop + plotHeight / 2f - measuredText.size.height / 2f // Y轴标题中心Y
                        ),
                        color = labelTextStyle.color ?: axisColor
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBarChartView() {
    val barData = BarChartData(
        labels = listOf("Apple", "Banana", "Orange", "Grape"),
        values = listOf(20f, 50f, 30f, 80f),
        colors = listOf(Color.Red, Color.Yellow, Color(0xFFFFA500), Color.Magenta),
        xAxisLabel = "水果",
        yAxisLabel = "销量",
        yAxisLabelOrientation = TextOrientation.Vertical // 预览默认竖向
    )
    BarChartView(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        data = barData
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewBarChartViewWithSmallBars() {
    val barData = BarChartData(
        labels = listOf("A", "B", "C", "D", "E"),
        values = listOf(5f, 15f, 10f, 25f, 7f),
        colors = listOf(Color.Green, Color.Blue, Color.Cyan, Color.DarkGray, Color.LightGray),
        barWidth = 16.dp,
        yAxisLabelOrientation = TextOrientation.Horizontal // 预览横向
    )
    BarChartView(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        data = barData
    )
}
