package top.itjl.ringchartview.demo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color // 确保导入 Color
// 您可能需要根据您的 colors.xml 定义更多颜色
// val Purple200 = Color(0xFFBB86FC)
// val Purple500 = Color(0xFF6200EE)
// val Purple700 = Color(0xFF3700B3)
// val Teal200 = Color(0xFF03DAC5)
// val Teal700 = Color(0xFF018786)
// val Black = Color(0xFF000000)
// val White = Color(0xFFFFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC), // 对应 res/values/colors.xml 中的 purple_200
    secondary = Color(0xFF03DAC5), // 对应 res/values/colors.xml 中的 teal_200
    tertiary = Color(0xFF03DAC5) // 您可以根据需要调整此颜色
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE), // 对应 res/values/colors.xml 中的 purple_500
    secondary = Color(0xFF03DAC5), // 对应 res/values/colors.xml 中的 teal_200
    tertiary = Color(0xFF03DAC5) // 您可以根据需要调整此颜色
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun RingChartViewLibTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography, // 使用默认排版，或定义您自己的
        content = content
    )
}