## RingChartView

[![JitPack](https://jitpack.io/v/kerwin162/RingChartView.svg)](https://jitpack.io/#kerwin162/RingChartView)   [English](README_en.md)

Android Jetpack Compose 自定义图表组件 - 支持环形图、饼形图、折线图和条形图，提供丰富的定制化配置和动画效果。本项目已从传统 Android View 系统迁移至 Jetpack Compose。

### 功能特性

- ✅ **环形图：** 全环形 & 半环形两种基础样式，单色/多段颜色进度条组合。
- ✅ **饼形图：** 支持实心饼图和空心厚环状（甜甜圈）两种形态，可定制中心内容。
- ✅ **折线图：** 支持单条/多条折线，可显示数据点，支持网格线和坐标轴标签。
- ✅ **条形图：** 支持垂直条形图，可定制条形宽度，支持网格线和坐标轴标签。
- 🎨 **通用样式定制：** 可定制画笔宽度、端点形状（圆头/平头）等。
- 📊 **最小粒度控制：** （仅环形图）解决极小进度不可见问题。
- 🚀 **动画效果：** 所有图表支持加载动画，值变化平滑过渡。
- 💡 **基于 Jetpack Compose 构建：** 拥抱现代 Android UI 开发。

### 快速接入

#### Step1. 添加JitPack仓库 (Project级 `settings.gradle.kts`)

请在您的项目根目录下的 `settings.gradle.kts` 文件中添加 JitPack 仓库：

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("[https://jitpack.io](https://jitpack.io)") } // 如果RingChartCompose库本身依赖jitpack，则需要
    }
}
```

#### Step2. 添加依赖 (Module级 `build.gradle.kts`)

在您需要使用 `RingChartView` 的模块（如 `app` 或 `demo` 模块）的 `build.gradle.kts` 文件中添加以下依赖：

```kotlin
dependencies {
    implementation 'com.github.kerwin162:RingChartView:{latest_version}'
}
```

*最新版本号请查看*[*JitPack徽章*](#ringchartview)

### 使用示例

`RingChartView`、`PieChartView`、`LineChartView` 和 `BarChartView` 都是 Jetpack Compose 可组合函数，直接在 Kotlin 代码中使用。

#### A. 环形图 (Ring Chart) 用法示例

##### I. Full Circle Chart (完整环形)

```kotlin
RingChartView(
    modifier = Modifier
        .fillMaxWidth() // 填充可用宽度
        .aspectRatio(1f) // 保持1:1的宽高比，确保图表为圆形
        .padding(20.dp),
    progress = 70,
    paintCap = StrokeCap.Round,
    chartAngleStyle = ChartAngleStyle.FullCircle,
    paintWidth = 15.dp,
    progressColor = Color(0xFF8BC34A) // holo_green_light
)
```

##### II.Half Circle Chart (半环形)

```kotlin
RingChartView(
    modifier = Modifier
        .fillMaxWidth() // 填充可用宽度
        .aspectRatio(1f) // 保持1:1的宽高比，确保图表为正方形区域
        .padding(20.dp),
    maxValue = 100,
    progress = 30,
    paintCap = StrokeCap.Square,
    chartAngleStyle = ChartAngleStyle.HalfCircle,
    paintWidth = 15.dp,
    progressColor = Color(0xFF8BC34A), // holo_green_light
    backColor = Color(0xFFFF9800) // holo_orange_light
)
```

##### III.Multi-Layer Progress Chart （多层分段）

```kotlin
val multiProgressNodes = listOf(
    ProgressNode(10f, Color(0xFF4CAF50)), // chart_green
    ProgressNode(20f, Color(0xFF2196F3)), // chart_blue
    ProgressNode(50f, Color(0xFFF44336)), // chart_red
    ProgressNode(10f, Color(0xFFFFEB3B))  // chart_yellow
)
RingChartView(
    modifier = Modifier
        .fillMaxWidth() // 填充可用宽度
        .aspectRatio(1f) // 保持1:1的宽高比，确保图表为正方形区域
        .padding(20.dp),
    maxValue = multiProgressNodes.sumOf { it.value.toDouble() }.toInt(), // 重新计算maxValue
    isMultiProgress = true,
    progressNodes = multiProgressNodes,
    chartAngleStyle = ChartAngleStyle.HalfCircle,
    paintWidth = 15.dp,
    backColor = Color(0xFFFF9800) // holo_orange_light
)
```

#### B. 饼形图 (Pie Chart) 用法示例

##### I. 实心饼图 (Solid Pie Chart)

```kotlin
val solidPieData = listOf(
    ProgressNode(value = 30f, color = Color.Red, label = "分类A"),
    ProgressNode(value = 20f, color = Color.Green, label = "分类B"),
    ProgressNode(value = 50f, color = Color.Blue, label = "分类C")
)
PieChartView(
    modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .padding(20.dp),
    data = solidPieData,
    holeRadius = 0.dp, // 设置为0dp表示实心
    showPercentageInCenter = true, // 显示中心百分比
    backColor = Color.White // 与容器背景色一致
)
```

##### II. 空心厚环状 (Donut Chart)

```kotlin
val donutPieData = listOf(
    ProgressNode(value = 40f, color = Color(0xFFFFA000), label = "项目X"),
    ProgressNode(value = 60f, color = Color(0xFF00ACC1), label = "项目Y")
)
PieChartView(
    modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .padding(20.dp),
    data = donutPieData,
    holeRadius = 80.dp, // 设置空心半径
    ringThickness = 40.dp, // 设置环的厚度
    showPercentageInCenter = true,
    backColor = Color.LightGray // 与容器背景色一致
)
```

#### C. 折线图 (Line Chart) 用法示例

```kotlin
val lineData = LineChartData(
    xLabels = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日"),
    yValues = listOf(
        listOf(10f, 40f, 25f, 60f, 35f, 70f, 55f), // 第一条线数据
        listOf(5f, 20f, 15f, 40f, 25f, 50f, 30f)  // 第二条线数据
    ),
    colors = listOf(Color.Blue, Color.Red), // 对应两条线的颜色
    xAxisLabel = "日期",
    yAxisLabel = "访问量",
    showPoints = true, // 显示数据点
    lineWidth = 2.dp // 线条宽度
)
LineChartView(
    modifier = Modifier
        .fillMaxWidth()
        .height(300.dp) // 指定高度
        .padding(16.dp),
    data = lineData,
    showGridLines = true // 显示网格线
)
```

#### D. 条形图 (Bar Chart) 用法示例

```kotlin
val barData = BarChartData(
    labels = listOf("Q1", "Q2", "Q3", "Q4"),
    values = listOf(120f, 180f, 150f, 200f),
    colors = listOf(Color.Green, Color.Yellow, Color.Cyan, Color.Magenta),
    xAxisLabel = "季度",
    yAxisLabel = "收入",
    barWidth = 24.dp // 条形宽度
)
BarChartView(
    modifier = Modifier
        .fillMaxWidth()
        .height(300.dp) // 指定高度
        .padding(16.dp),
    data = barData,
    showGridLines = true // 显示网格线
)
```

### API 参考手册

#### `RingChartView` 参数

| 参数名称          | 类型                                 | 描述                                                         | 默认值                       |
| ----------------- | ------------------------------------ | ------------------------------------------------------------ | ---------------------------- |
| `modifier`        | `Modifier`                           | Compose 修饰符                                               | `Modifier`                   |
| `maxValue`        | `Int`                                | 图表的最大刻度值                                             | `100`                        |
| `progress`        | `Int`                                | 当前进度值（单色模式下有效）                                 | `0`                          |
| `minProgress`     | `Float`                              | 可视化的最小进度颗粒度                                       | `1f`                         |
| `paintCap`        | `StrokeCap`                          | 线段端头风格 (`StrokeCap.Round`/`StrokeCap.Square`)          | `StrokeCap.Round`            |
| `paintWidth`      | `Dp`                                 | 线段粗细（单位：dp）                                         | `40.dp`                      |
| `backColor`       | `androidx.compose.ui.graphics.Color` | 背景色                                                       | `Color.LightGray`            |
| `progressColor`   | `androidx.compose.ui.graphics.Color` | 前景色（单色进度条颜色）                                     | `Color.Green`                |
| `chartAngleStyle` | `ChartAngleStyle`                    | 图表角度样式 (`ChartAngleStyle.HalfCircle`/`ChartAngleStyle.FullCircle`) | `ChartAngleStyle.HalfCircle` |
| `drawStartAngle`  | `Float`                              | 绘制起始角度（默认180°水平起始）                             | `180f`                       |
| `isMultiProgress` | `Boolean`                            | 是否开启多段式进度条模式                                     | `false`                      |
| `progressNodes`   | `List<ProgressNode>`                 | 多段进度条数据列表（`isMultiProgress` 为 `true` 时有效）     | `emptyList()`                |
| `animationTime`   | `Int`                                | 动画持续时间（毫秒）                                         | `1200`                       |
| `protectMinValue` | `Boolean`                            | 是否保护最小值，确保即使极小值也能显示                       | `true`                       |

#### `PieChartView` 参数

| 参数名称          | 类型                                 | 描述                                                         | 默认值                          |
| ----------------- | ------------------------------------ | ------------------------------------------------------------ | ------------------------------- |
| `modifier`        | `Modifier`                           | Compose 修饰符                                               | `Modifier`                      |
| `data`            | `List<ProgressNode>`                 | 饼图数据列表，`ProgressNode` 包含 `value`、`color` 和可选 `label` | `emptyList()`                   |
| `animationTime`   | `Int`                                | 动画持续时间（毫秒）                                         | `1200`                          |
| `holeRadius`      | `Dp`                                 | 中心孔半径。`0.dp` 为实心饼图，大于 `0.dp` 为甜甜圈模式。     | `0.dp`                          |
| `ringThickness`   | `Dp`                                 | 甜甜圈环的厚度。仅在 `holeRadius > 0.dp` 时有效。             | `40.dp`                         |
| `labelTextStyle`  | `TextStyle`                          | 扇区标签文本样式                                             | `TextStyle(12.sp, Color.Black)` |
| `showPercentageInCenter` | `Boolean`                     | 是否在中心显示总百分比                                       | `false`                         |
| `centerContent`   | `@Composable (() -> Unit)?`          | 饼图中心的自定义 Composable 内容                           | `null`                          |
| `backColor`       | `androidx.compose.ui.graphics.Color` | 饼图背景色，用于甜甜圈模式下绘制中心孔                       | `Color.White`                   |

#### `LineChartData` 数据模型

| 属性名称      | 类型                  | 描述                  | 默认值 |
| ----------- | --------------------- | --------------------- | ------ |
| `xLabels`   | `List<String>`        | X轴标签列表           |        |
| `yValues`   | `List<List<Float>>`   | 每条折线的数据点列表    |        |
| `colors`    | `List<Color>`         | 每条折线的颜色列表      |        |
| `showPoints` | `Boolean`             | 是否显示数据点        | `true` |
| `pointRadius` | `Dp`                 | 数据点半径            | `4.dp` |
| `lineWidth` | `Dp`                  | 折线宽度              | `2.dp` |
| `yAxisLabel` | `String?`             | Y轴整体描述           | `null` |
| `xAxisLabel` | `String?`             | X轴整体描述           | `null` |

#### `LineChartView` 参数

| 参数名称          | 类型                                 | 描述                               | 默认值                       |
| ----------------- | ------------------------------------ | ---------------------------------- | ---------------------------- |
| `modifier`        | `Modifier`                           | Compose 修饰符                     | `Modifier`                   |
| `data`            | `LineChartData`                      | 折线图数据                         |                              |
| `animationTime`   | `Int`                                | 动画持续时间（毫秒）               | `1200`                       |
| `showGridLines`   | `Boolean`                            | 是否显示网格线                     | `true`                       |
| `axisColor`       | `androidx.compose.ui.graphics.Color` | 坐标轴和网格线颜色                 | `Color.Gray`                 |
| `labelTextStyle`  | `TextStyle`                          | 坐标轴标签文本样式                 | `TextStyle(10.sp, Color.Black)` |

#### `BarChartData` 数据模型

| 属性名称    | 类型             | 描述           | 默认值   |
| --------- | ---------------- | -------------- | -------- |
| `labels`  | `List<String>`   | 每个条形柱的标签 |          |
| `values`  | `List<Float>`    | 每个条形柱的值   |          |
| `colors`  | `List<Color>`    | 每个条形柱的颜色 |          |
| `barWidth` | `Dp`            | 条形柱的宽度     | `24.dp`  |
| `xAxisLabel` | `String?`       | X轴整体描述    | `null`   |
| `yAxisLabel` | `String?`       | Y轴整体描述    | `null`   |

#### `BarChartView` 参数

| 参数名称          | 类型                                 | 描述                               | 默认值                       |
| ----------------- | ------------------------------------ | ---------------------------------- | ---------------------------- |
| `modifier`        | `Modifier`                           | Compose 修饰符                     | `Modifier`                   |
| `data`            | `BarChartData`                       | 条形图数据                         |                              |
| `animationTime`   | `Int`                                | 动画持续时间（毫秒）               | `1200`                       |
| `showGridLines`   | `Boolean`                            | 是否显示网格线                     | `true`                       |
| `axisColor`       | `androidx.compose.ui.graphics.Color` | 坐标轴和网格线颜色                 | `Color.Gray`                 |
| `labelTextStyle`  | `TextStyle`                          | 坐标轴标签文本样式                 | `TextStyle(10.sp, Color.Black)` |

---

**[🐛 Issue Tracker]** [https://github.com/kerwin162/RingChartView/issues](https://github.com/ispace-top/RingChartView/issues)

*欢迎 Star⭐️ & PR！*
