package top.itjl.ringchart.util

import android.content.Context
import android.content.pm.ApplicationInfo

// 在Compose中，通常不需要像传统View那样显式判断isDebug
// 因为Compose的Preview功能已经提供了很好的预览能力
// 如果确实需要，可以在ApplicationInfo中获取
fun isDebuggable(context: Context): Boolean {
    return (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
}