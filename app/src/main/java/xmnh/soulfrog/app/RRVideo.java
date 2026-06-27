package xmnh.soulfrog.app;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.SoulFrog;
import xmnh.soulfrog.interfaces.BaseHook;
import xmnh.soulfrog.utils.HookUtil;

public class RRVideo implements BaseHook {

    @Override
    public void hook(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        Class<?> appCommonUtils = HookUtil.findClassIfExists("com.pptv.common.utils.AppCommonUtils", classLoader);
        if (appCommonUtils == null) return;
        try {
            Method checkUserPrivilege = appCommonUtils.getDeclaredMethod("checkUserPrivilege", String.class);
            HookUtil.replaceReturnValue(xposedModule, checkUserPrivilege, true);
            Method isVip = appCommonUtils.getDeclaredMethod("isVip");
            HookUtil.replaceReturnValue(xposedModule, isVip, true);
            Class<?> vipInfo = classLoader.loadClass("com.pptv.common.data.bean.VipInfo");
            Method getValid = vipInfo.getDeclaredMethod("getValid");
            HookUtil.replaceReturnValue(xposedModule, getValid, true);
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "RRVideo hook error", e);
        }

    }

}
