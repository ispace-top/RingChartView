package top.itjl.ringchart.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 折线图数据模型。
 * @param xLabels X轴标签列表，例如日期、类别。
 * @param yValues 每条折线的数据点列表。
 * @param colors 每条折线的颜色列表，与yValues的外部列表对应。
 * @param showPoints 是否显示数据点。
 * @param pointRadius 数据点的半径。
 * @param lineWidth 折线的宽度。
 * @param yAxisLabel Y轴的整体描述。
 * @param xAxisLabel X轴的整体描述。
 * @param yAxisLabelOrientation Y轴标签的显示方向，默认为Vertical。
 */
data class LineChartData(
    val xLabels: List<String>,
    val yValues: List<List<Float>>, // 支持多条折线
    val colors: List<Color>,
    val showPoints: Boolean = true,
    val pointRadius: Dp = 4.dp,
    val lineWidth: Dp = 2.dp,
    val yAxisLabel: String? = null, // Y轴的整体描述
    val xAxisLabel: String? = null, // X轴的整体描述
    val yAxisLabelOrientation: TextOrientation = TextOrientation.Vertical // 默认竖向显示
)
