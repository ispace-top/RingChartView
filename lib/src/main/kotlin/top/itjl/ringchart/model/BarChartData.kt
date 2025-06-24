package top.itjl.ringchart.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 条形图数据模型。
 * @param labels 每个条形柱的标签。
 * @param values 每个条形柱的值。
 * @param colors 每个条形柱的颜色。
 * @param barWidth 条形柱的宽度。
 * @param xAxisLabel X轴的整体描述。
 * @param yAxisLabel Y轴的整体描述。
 * @param yAxisLabelOrientation Y轴标签的显示方向，默认为Vertical。
 */
data class BarChartData(
    val labels: List<String>,
    val values: List<Float>,
    val colors: List<Color>,
    val barWidth: Dp = 24.dp,
    val xAxisLabel: String? = null,
    val yAxisLabel: String? = null,
    val yAxisLabelOrientation: TextOrientation = TextOrientation.Vertical // 默认竖向显示
)
