package top.itjl.ringchartview.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import top.itjl.ringchart.component.ChartAngleStyle
import top.itjl.ringchart.component.RingChartView
import top.itjl.ringchart.model.ProgressNode
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
                    RingChartDemoScreen()
                }
            }
        }
    }
}

@Composable
fun RingChartDemoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // 全环形图
        RingChartView(
            modifier = Modifier
                .fillMaxWidth() // 填充可用宽度
                .aspectRatio(1f) // 保持1:1的宽高比，使其变为正方形
                .padding(20.dp),
            progress = 70,
            chartAngleStyle = ChartAngleStyle.FullCircle,
            paintWidth = 15.dp,
            progressColor = Color(0xFF8BC34A), // holo_green_light
            backColor = Color(0xFFFF9800) // holo_orange_light
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 半环形图
        RingChartView(
            modifier = Modifier
                .fillMaxWidth() // 填充可用宽度
                .aspectRatio(1f) // 保持1:1的宽高比，使其变为正方形
                .padding(20.dp),
            maxValue = 100,
            progress = 30,
            paintCap = androidx.compose.ui.graphics.StrokeCap.Square,
            chartAngleStyle = ChartAngleStyle.HalfCircle,
            paintWidth = 15.dp,
            progressColor = Color(0xFF8BC34A), // holo_green_light
            backColor = Color(0xFFFF9800) // holo_orange_light
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 多段进度条
        val multiProgressNodes = listOf(
            ProgressNode(10f, Color(0xFF4CAF50)), // chart_green
            ProgressNode(20f, Color(0xFF2196F3)), // chart_blue
            ProgressNode(50f, Color(0xFFF44336)), // chart_red
            ProgressNode(10f, Color(0xFFFFEB3B))  // chart_yellow
        )
        RingChartView(
            modifier = Modifier
                .fillMaxWidth() // 填充可用宽度
                .aspectRatio(1f) // 保持1:1的宽高比，使其变为正方形
                .padding(20.dp),
            maxValue = multiProgressNodes.sumOf { it.value.toDouble() }.toInt(), // 重新计算maxValue
            isMultiProgress = true,
            progressNodes = multiProgressNodes,
            chartAngleStyle = ChartAngleStyle.HalfCircle,
            paintWidth = 15.dp,
            backColor = Color(0xFFFF9800) // holo_orange_light
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RingChartViewLibTheme {
        RingChartDemoScreen()
    }
}