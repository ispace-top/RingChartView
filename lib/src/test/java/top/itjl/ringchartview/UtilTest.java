package top.itjl.ringchartview;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import androidx.annotation.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UtilTest {
    @Mock
    Context mockContext;

    @Test(expected = IllegalArgumentException.class)
    public void isDebug_shouldThrowWhenNullContext() {
        Util.isDebug(null);
    }

    @Test
    public void isDebug_returnsTrueWhenDebuggable() {
        ApplicationInfo appInfo = new ApplicationInfo();
        appInfo.flags = ApplicationInfo.FLAG_DEBUGGABLE;
        when(mockContext.getApplicationInfo()).thenReturn(appInfo);

        assertTrue(Util.isDebug(mockContext));
    }

    @Test
    public void isDebug_returnsFalseWhenNotDebuggable() {
        ApplicationInfo appInfo = new ApplicationInfo();
        appInfo.flags = 0;
        when(mockContext.getApplicationInfo()).thenReturn(appInfo);

        assertFalse(Util.isDebug(mockContext));
    }

    @Test
    public void isDebug_usesCacheOnSecondCall() {
        ApplicationInfo appInfo = new ApplicationInfo();
        appInfo.flags = ApplicationInfo.FLAG_DEBUGGABLE;
        when(mockContext.getApplicationInfo()).thenReturn(appInfo);

        // 首次调用
        Util.isDebug(mockContext);
        // 二次调用应使用缓存
        Util.isDebug(mockContext);
        
        verify(mockContext, times(1)).getApplicationInfo();
    }
}