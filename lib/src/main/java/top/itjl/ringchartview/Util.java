package top.itjl.ringchartview;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import java.lang.ref.SoftReference;

/**
 * 工具类
 * <p>
 * 包含调试模式检测工具方法
 */
public class Util {
    private static SoftReference<ApplicationInfo> cachedAppInfo = new SoftReference<>(null);

    /**
     * 检测当前应用是否处于调试模式
     *
     * @param context 上下文对象（非空）
     * @return true表示处于调试模式
     * @throws IllegalArgumentException 如果context参数为null
     */
    public static boolean isDebug(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        ApplicationInfo appInfo = cachedAppInfo.get();
        if (appInfo == null) {
            appInfo = context.getApplicationInfo();
            cachedAppInfo = new SoftReference<>(appInfo);
        }

        return appInfo != null && isDebuggableFlagSet(appInfo);
    }

    private static boolean isDebuggableFlagSet(ApplicationInfo appInfo) {
        return (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}
