package top.itjl.ringchartview.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.itjl.ringchart.component.ChartAngleStyle
import top.itjl.ringchart.component.BarChartView
import top.itjl.ringchart.component.LineChartView
import top.itjl.ringchart.component.PieChartView
import top.itjl.ringchart.component.RingChartView
import top.itjl.ringchart.model.BarChartData
import top.itjl.ringchart.model.LineChartData
import top.itjl.ringchart.model.ProgressNode
import top.itjl.ringchart.model.TextOrientation // 导入 TextOrientation
import top.itjl.ringchartview.demo.ui.theme.RingChartViewLibTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RingChartViewLibTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChartDemoScreen()
                }
            }
        }
    }
}

@Composable
fun ChartDemoScreen() {
    val containerBackgroundColor = Color.LightGray.copy(alpha = 0.1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // --- 环形图示例 ---
        Text(text = "环形图示例", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
        RingChartView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(20.dp)
                .background(containerBackgroundColor),
            progress = 70,
            chartAngleStyle = ChartAngleStyle.FullCircle,
            paintWidth = 15.dp,
            progressColor = Color(0xFF8BC34A),
            backColor = Color(0xFFFF9800)
        )

        Spacer(modifier = Modifier.height(20.dp))

        RingChartView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(20.dp)
                .background(containerBackgroundColor),
            maxValue = 100,
            progress = 30,
            paintCap = StrokeCap.Square,
            chartAngleStyle = ChartAngleStyle.HalfCircle,
            paintWidth = 15.dp,
            progressColor = Color(0xFF8BC34A),
            backColor = Color(0xFFFF9800)
        )

        Spacer(modifier = Modifier.height(20.dp))

        val multiProgressNodes = listOf(
            ProgressNode(10f, Color(0xFF4CAF50), label = "绿色"),
            ProgressNode(20f, Color(0xFF2196F3), label = "蓝色"),
            ProgressNode(50f, Color(0xFFF44336), label = "红色"),
            ProgressNode(10f, Color(0xFFFFEB3B), label = "黄色")
        )
        RingChartView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(20.dp)
                .background(containerBackgroundColor),
            maxValue = multiProgressNodes.sumOf { it.value.toDouble() }.toInt(),
            isMultiProgress = true,
            progressNodes = multiProgressNodes,
            chartAngleStyle = ChartAngleStyle.HalfCircle,
            paintWidth = 15.dp,
            backColor = Color(0xFFFF9800)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // --- 饼形图示例 ---
        Text(text = "饼形图示例", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))

        Text(text = "实心饼图 (Solid Pie Chart)", fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
        val solidPieData = listOf(
            ProgressNode(value = 30f, color = Color.Red, label = "分类A"),
            ProgressNode(value = 20f, color = Color.Green, label = "分类B"),
            ProgressNode(value = 50f, color = Color.Blue, label = "分类C")
        )
        PieChartView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(20.dp)
                .background(containerBackgroundColor),
            data = solidPieData,
            holeRadius = 0.dp,
            showPercentageInCenter = true,
            backColor = containerBackgroundColor
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "空心厚环状 (Donut Chart)", fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
        val donutPieData = listOf(
            ProgressNode(value = 40f, color = Color(0xFFFFA000), label = "项目X"),
            ProgressNode(value = 60f, color = Color(0xFF00ACC1), label = "项目Y")
        )
        PieChartView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(20.dp)
                .background(containerBackgroundColor),
            data = donutPieData,
            holeRadius = 80.dp,
            ringThickness = 40.dp,
            showPercentageInCenter = true,
            backColor = containerBackgroundColor
        )

        Spacer(modifier = Modifier.height(40.dp))

        // --- 折线图示例 ---
        Text(text = "折线图示例", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))

        // 折线图 - Y轴标签竖向 (默认)
        Text(text = "Y轴标签竖向 (默认)", fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
        val lineDataVertical = LineChartData(
            xLabels = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日"),
            yValues = listOf(
                listOf(10f, 40f, 25f, 60f, 35f, 70f, 55f),
                listOf(5f, 20f, 15f, 40f, 25f, 50f, 30f)
            ),
            colors = listOf(Color.Blue, Color.Red),
            xAxisLabel = "日期",
            yAxisLabel = "访问量",
            yAxisLabelOrientation = TextOrientation.Vertical // 明确设置为竖向
        )
        LineChartView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp)
                .background(containerBackgroundColor),
            data = lineDataVertical,
            showGridLines = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 折线图 - Y轴标签横向
        Text(text = "Y轴标签横向", fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
        val lineDataHorizontal = LineChartData(
            xLabels = listOf("Q1", "Q2", "Q3", "Q4"),
            yValues = listOf(listOf(100f, 150f, 120f, 180f)),
            colors = listOf(Color.Green),
            xAxisLabel = "季度",
            yAxisLabel = "销售额",
            yAxisLabelOrientation = TextOrientation.Horizontal // 明确设置为横向
        )
        LineChartView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp)
                .background(containerBackgroundColor),
            data = lineDataHorizontal,
            showGridLines = true
        )

        Spacer(modifier = Modifier.height(40.dp))

        // --- 条形图示例 ---
        Text(text = "条形图示例", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))

        // 条形图 - Y轴标签竖向 (默认)
        Text(text = "Y轴标签竖向 (默认)", fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
        val barDataVertical = BarChartData(
            labels = listOf("产品A", "产品B", "产品C", "产品D"),
            values = listOf(20f, 50f, 30f, 80f),
            colors = listOf(Color.Red, Color.Yellow, Color.Blue, Color.Magenta),
            xAxisLabel = "产品",
            yAxisLabel = "库存量",
            yAxisLabelOrientation = TextOrientation.Vertical // 明确设置为竖向
        )
        BarChartView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp)
                .background(containerBackgroundColor),
            data = barDataVertical,
            showGridLines = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 条形图 - Y轴标签横向
        Text(text = "Y轴标签横向", fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
        val barDataHorizontal = BarChartData(
            labels = listOf("部门1", "部门2", "部门3"),
            values = listOf(60f, 90f, 45f),
            colors = listOf(Color.Cyan, Color.LightGray, Color.Green),
            xAxisLabel = "部门",
            yAxisLabel = "预算",
            yAxisLabelOrientation = TextOrientation.Horizontal // 明确设置为横向
        )
        BarChartView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp)
                .background(containerBackgroundColor),
            data = barDataHorizontal,
            showGridLines = true
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultChartPreview() {
    RingChartViewLibTheme {
        ChartDemoScreen()
    }
}
