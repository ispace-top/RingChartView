## RingChartView
[![JitPack](https://jitpack.io/v/kerwin162/RingChartView.svg)](https://jitpack.io/#kerwin162/RingChartView)

[中文](.README.md)

Android Custom Circular Chart Component - Supports Full/Half Ring Styles, Multi-Segment Progress Display, and Rich Customization Options

### Features
- ✅ Full-Ring & Half-Ring Base Styles
- 🎨 Single/Multi-Colored Progress Segments
- 🔧 Customizable Stroke Width and Cap Style (Round/Butt)
- 📊 Minimum Granularity Control (Solves Tiny Progress Visibility Issues)
- ⚡ Dual Configuration Modes: XML Attributes + Java Dynamic Settings

### Quick Integration
#### Step1. Add JitPack Repository (Project-level build.gradle)
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

#### Step2. Add Dependency (Module-level build.gradle)
```gradle
dependencies {
    implementation 'com.github.kerwin162:RingChartView:{latest_version}'
}
```
*Check latest version from [JitPack badge](#ringchartview)*

### Usage Examples
#### A. XML Layout Implementation

##### I. Full Circle Chart 
```xml
<top.itjl.ringchartview.RingChartView 
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:progress="70"
    app:paintCap="ROUND"
    app:chartAngleStyle="FULL_CIRCLE"
    app:paintWidth="15dp"
    app:progressColor="@android:color/holo_green_light"/>
```
<img src="pic/full_circle.png" width=220 alt="Full Circle Demo"/>

##### II.Half Circle Chart 
```xml 
<top.itjl.ringchartview.RingChartView 
    ...
app:maxValue="100"    
app:chartAngleStyle="HALF_CIRCLE"/>
```
<img src="/pic/half_circle02.png" width=220 alt=Half-Circle-Demo />

---

#### B.Java Dynamic Configuration（Multi-Segment）

##### III.Multi-Layer Progress Chart 
1.XML Basic Setup：
```xml 
<top.itjl.ringchartview.RingChartView  
   ...   
app:multiProgress=true />
```

2.Java Code Setup：
```java 
List<RingChartView.ProgressNode> nodeList = new ArrayList<>();
nodeList.add(new ProgressNode(10, Color.GREEN)); //value + color  
nodeList.add(new ProgressNode(20, Color.BLUE));
//...other nodes  

yourRingChart.setProgressNodes(nodeList);
```
<img src="/pic/half_circle01.png" width=220 alt=MultiLayer-Demo />

---

### API Reference Manual  
| Attribute         | Description                          | Default       | XML Support |
|-------------------|--------------------------------------|---------------|-------------|
| maxValue          | Maximum scale value                 | `100`         | ✅           |
| progress          | Current progress value              | `0`           | ✅           |
| minProgress       | Minimum visible granularity         | `1`           | ✅           |
| paintCap          | Line cap style (`ROUND/SQUARE`)     | ROUND        | ✅         |           
|paintWidth         │ Stroke thickness (unit：dp/dimen)   │ 40dp         │ ✅         │           
|backColor           │ Background color                    │ Color.LTGRAY  │✅          │               
|progressColor       │ Foreground color                    │ Color.GREEN   │✅          │               
|multiProgress       | Enable multi-segment mode            | false        | ❌(Use setter method) |

**Advanced Methods**:
```java 
// Set multiple segments data （Require enabling multiProgress first）
void setProgressNodes(List<ProgressNode> nodes)

// Update single progress value （Valid in single-color mode）
void setCurrentValue(int value) 

// Customize start angle for half-circle （Default：180° horizontal start）
void setStartAngle(float degree)  
```

---

### Q&A Frequently Asked Questions
 
Q：Why small values are not displayed?
A：Ensure minProgess ≤ target value; If still invisible try increasing paintWidth
 
Q：How to implement gradient effect?
A：Currently unsupported; Planned for v2.x release
 
Q：Can I adjust starting angle?  
A: Yes! Use setStartAngle() method


---
**[🐛 Issue Tracker]** [https://github.com/kerwin162/RingChar​tVie​w/issues](https://github.com/ispace-top/RingChartView/issues)<br>


*Welcome to Star⭐️ & Contribute!*
