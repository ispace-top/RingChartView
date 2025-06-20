## RingChartView

[English](README_en.md)

Android Jetpack Compose 自定义环形图表组件 - 支持全环/半环样式、多段进度条显示及丰富定制化配置。本项目已从传统 Android View 系统迁移至 Jetpack Compose。

### 功能特性

- ✅ 全环形 & 半环形两种基础样式
- 🎨 单色/多段颜色进度条组合
- 🔧 可定制的画笔宽度与端点形状（圆头/平头）
- 📊 最小粒度控制（解决极小进度不可见问题）
- 🚀 基于 Jetpack Compose 构建，拥抱现代 Android UI 开发

### 快速接入

#### Step1. 添加JitPack仓库 (Project级 `settings.gradle.kts`)

请在您的项目根目录下的 `settings.gradle.kts` 文件中添加 JitPack 仓库：

```
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

```
dependencies {
    implementation 'com.github.kerwin162:RingChartView:{latest_version}'
}
```

*最新版本号请查看*[*JitPack徽章*](#ringchartview)

### 使用示例

`RingChartView` 现在是一个 Jetpack Compose 可组合函数，直接在 Kotlin 代码中使用。

#### A. Compose 用法示例

##### I. Full Circle Chart (完整环形)

```
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

```
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

```
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

### API 参考手册

以下是 `RingChartView` Compose 可组合函数的参数列表及其描述：

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

**数据模型：** `ProgressNode` 数据类定义了多段进度的数据结构：

```
data class ProgressNode(val value: Float, val color: Color)
```

### Q&A 常见问题

Q：为什么设置了小数值没有显示？ A：确保 `minProgress` 值小于等于目标值；若仍无效尝试增大 `paintWidth` 尺寸。

Q：如何实现渐变效果？ A：目前暂不支持渐变色填充，计划 v2.x 版本将增加此特性。

Q：能否调整起始角度？ A：是的，可以通过 `drawStartAngle` 参数进行角度调整。

### 贡献与问题追踪

**[🐛 Issue Tracker]** [https://github.com/kerwin162/RingChartView/issues](https://github.com/ispace-top/RingChartView/issues)

*欢迎 Star⭐️ & PR！*