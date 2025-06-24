package top.itjl.ringchart.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.itjl.ringchart.model.ProgressNode
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// 默认值，为了保持一致性
private const val DEFAULT_PIE_RING_THICKNESS_DP = 40f // 用于甜甜圈默认环的厚度

/**
 * 饼形图组件。
 *
 * @param modifier Composable修饰符。
 * @param data 饼形图的数据列表，每个ProgressNode代表一个扇区。
 * @param animationTime 动画持续时间（毫秒）。
 * @param holeRadius 饼图中心空白圆的半径。设置为0.dp则为实心饼图，大于0.dp则为甜甜圈模式。
 * @param ringThickness 绘制甜甜圈时环的厚度。仅当holeRadius > 0.dp时有效。
 * 此参数将定义甜甜圈的实际填充厚度。
 * @param labelTextStyle 扇区标签的文本样式。
 * @param showPercentageInCenter 是否在中心显示总百分比。
 * @param centerContent 如果需要，可以在中心自定义Composable内容。
 * @param backColor 饼图的背景色，用于甜甜圈模式下绘制背景环（如果需要，并且是透明的甜甜圈）。
 */
@Composable
fun PieChartView(
    modifier: Modifier = Modifier,
    data: List<ProgressNode>,
    animationTime: Int = 1200,
    holeRadius: Dp = 0.dp, // 中心圆的半径，0表示实心饼图
    ringThickness: Dp = DEFAULT_PIE_RING_THICKNESS_DP.dp, // 甜甜圈环的厚度，新参数名
    labelTextStyle: TextStyle = TextStyle(fontSize = 12.sp, color = Color.Black),
    showPercentageInCenter: Boolean = false,
    centerContent: @Composable (() -> Unit)? = null,
    backColor: Color = Color.White // 用于实心饼图背景或甜甜圈背景环
) {
    val animatedPhase = remember { Animatable(0f) }
    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(data) {
        animatedPhase.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animationTime, easing = LinearEasing)
        )
    }

    val totalValue = data.sumOf { it.value.toDouble() }.toFloat()
    if (totalValue == 0f && data.isNotEmpty()) {
        BoxWithConstraints(modifier = modifier) {
            Text(text = "数据总和为零，无法绘制饼图。", modifier = Modifier.align(Alignment.Center))
        }
        return
    }
    if (data.isEmpty()) {
        BoxWithConstraints(modifier = modifier) {
            Text(text = "无数据可绘制饼图。", modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    val localDensity = LocalDensity.current

    BoxWithConstraints(modifier = modifier) {
        val minSize = min(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        val outerDiameter = minSize
        val centerX = minSize / 2f
        val centerY = minSize / 2f

        val calculatedRingThicknessPx = with(localDensity) { ringThickness.toPx() } // 环的像素厚度
        val effectiveHoleRadiusPx = with(localDensity) { holeRadius.toPx() } // 甜甜圈内孔半径像素值

        // 甜甜圈模式下的外半径，是内孔半径加上环厚度
        // 或者对于实心饼图，就是整个半径
        val currentOuterRadiusPx = if (holeRadius > 0.dp) {
            effectiveHoleRadiusPx + calculatedRingThicknessPx
        } else {
            outerDiameter / 2f // 实心饼图的半径
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            var currentStartAngle = 0f

            // 甜甜圈模式下，先绘制一个背景环，确保甜甜圈底色
            if (holeRadius > 0.dp) {
                drawArc(
                    color = backColor, // 甜甜圈的背景环颜色
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false, // 不连接中心
                    topLeft = Offset(centerX - currentOuterRadiusPx, centerY - currentOuterRadiusPx),
                    size = Size(currentOuterRadiusPx * 2, currentOuterRadiusPx * 2),
                    style = Stroke(width = calculatedRingThicknessPx)
                )
            }

            data.forEach { node ->
                val sweepAngle = (node.value / totalValue) * 360f * animatedPhase.value

                if (holeRadius == 0.dp) { // 实心饼图 (Solid Pie Chart)
                    drawArc(
                        color = node.color,
                        startAngle = currentStartAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true, // 连接到中心，形成实心扇形
                        topLeft = Offset(centerX - currentOuterRadiusPx, centerY - currentOuterRadiusPx),
                        size = Size(currentOuterRadiusPx * 2, currentOuterRadiusPx * 2),
                        style = androidx.compose.ui.graphics.drawscope.Fill // 完全填充
                    )
                } else { // 空心厚环状 (Donut Chart) - 使用 Stroke 绘制带厚度的环段
                    drawArc(
                        color = node.color,
                        startAngle = currentStartAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false, // 不连接中心
                        // 绘制区域的 topLeft 和 size 需要根据 Stroke 的宽度来计算，使其居中于环的厚度
                        topLeft = Offset(
                            centerX - (effectiveHoleRadiusPx + calculatedRingThicknessPx / 2f),
                            centerY - (effectiveHoleRadiusPx + calculatedRingThicknessPx / 2f)
                        ),
                        size = Size(
                            (effectiveHoleRadiusPx + calculatedRingThicknessPx / 2f) * 2,
                            (effectiveHoleRadiusPx + calculatedRingThicknessPx / 2f) * 2
                        ),
                        style = Stroke(width = calculatedRingThicknessPx)
                    )
                }

                // 绘制扇区标签 (可选)
                if (node.label != null) {
                    val labelAngle = currentStartAngle + sweepAngle / 2f
                    // 标签位置根据是否是甜甜圈模式进行调整
                    val currentLabelRadius = if (holeRadius > 0.dp) {
                        // 甜甜圈模式下，标签在内孔和外径的中间
                        effectiveHoleRadiusPx + calculatedRingThicknessPx / 2f
                    } else {
                        currentOuterRadiusPx * 0.7f // 实心饼图标签位置
                    }

                    val x = centerX + currentLabelRadius * cos(Math.toRadians(labelAngle.toDouble())).toFloat()
                    val y = centerY + currentLabelRadius * sin(Math.toRadians(labelAngle.toDouble())).toFloat()

                    val textLayoutResult = textMeasurer.measure(
                        text = node.label,
                        style = labelTextStyle
                    )
                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(x - textLayoutResult.size.width / 2, y - textLayoutResult.size.height / 2),
                        color = labelTextStyle.color ?: Color.Black
                    )
                }

                currentStartAngle += sweepAngle
            }
        }

        // 绘制中心内容 (可选)
        if (showPercentageInCenter || centerContent != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(holeRadius / 2f), // 根据中心圆半径调整内边距
                contentAlignment = Alignment.Center
            ) {
                if (showPercentageInCenter) {
                    val currentPercentage = (animatedPhase.value * 100).toInt()
                    Text(
                        text = "${currentPercentage}%",
                        style = labelTextStyle.copy(fontSize = 24.sp) // 中心百分比可以更大
                    )
                }
                centerContent?.invoke()
            }
        }
    }
}

// Update Preview functions with new 'ringThickness' and 'backColor' parameters

@Preview(showBackground = true)
@Composable
fun PreviewPieChartView() {
    val pieData = listOf(
        ProgressNode(value = 30f, color = Color.Red, label = "数据A"),
        ProgressNode(value = 20f, color = Color.Green, label = "数据B"),
        ProgressNode(value = 50f, color = Color.Blue, label = "数据C")
    )
    PieChartView(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        data = pieData,
        animationTime = 1500,
        holeRadius = 80.dp, // 示例：甜甜圈效果的内径
        ringThickness = 40.dp, // 示例：甜甜圈环的厚度
        showPercentageInCenter = true,
        backColor = Color.LightGray // 示例背景色，应与容器背景色匹配
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewPieChartViewSolid() {
    val pieData = listOf(
        ProgressNode(value = 40f, color = Color.Cyan, label = "项目X"),
        ProgressNode(value = 60f, color = Color.Magenta, label = "项目Y")
    )
    PieChartView(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        data = pieData,
        animationTime = 1000,
        holeRadius = 0.dp, // 示例：实心饼图
        showPercentageInCenter = false,
        backColor = Color.White // 示例背景色
    )
}
