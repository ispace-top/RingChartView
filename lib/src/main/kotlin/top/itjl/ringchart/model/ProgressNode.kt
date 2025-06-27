package top.itjl.ringchart.model

import androidx.compose.ui.graphics.Color

/**
 * 进度节点数据模型，适用于环形图和饼图的单个扇区/进度段。
 * @param value 节点的值。
 * @param color 节点的颜色。
 * @param label 可选的标签，用于显示在图表上或图例中。
 */
data class ProgressNode(val value: Float, val color: Color, val label: String? = null)
