package xmnh.soulfrog.game;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.SoulFrog;
import xmnh.soulfrog.utils.HookUtil;

public class Game4399 {
    public static void hook(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        try {
            check(xposedModule, context, classLoader);
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "BiLiBiLi hook error", e);
        }
    }

    private static void check(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        Class<?> dlcSkuState = HookUtil.findClassIfExists("cn.m4399.operate.api.DLCSkuState", classLoader);
        if (dlcSkuState == null) return;
        try {
            Class<?> fnSpecialAdapter4399Hezi = classLoader.loadClass("com.ssjj.fnsdk.platform.FNSpecialAdapter4399Hezi");
            Method queryDlc = fnSpecialAdapter4399Hezi.getDeclaredMethod("getDlcList",
                    classLoader.loadClass("com.ssjj.fnsdk.core.SsjjFNParams"),
                    classLoader.loadClass("com.ssjj.fnsdk.core.SsjjFNListener"));
            xposedModule.hook(queryDlc).intercept(chain -> {
                Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain));
                return chain.proceed();
            });
            Method isPurchased = dlcSkuState.getDeclaredMethod("isPurchased");
            HookUtil.replaceReturnValue(xposedModule, isPurchased, true);
            Class<?> dlc = classLoader.loadClass("cn.m4399.operate.api.DLC");
            Class<?> opeDataListener = classLoader.loadClass("cn.m4399.operate.api.OpeDataListener");
            Method querySkuList = dlc.getDeclaredMethod("querySkuList", opeDataListener);
            xposedModule.hook(querySkuList).intercept(chain -> {
                Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain));
                return chain.proceed();
            });
            Method querySkuState = dlc.getDeclaredMethod("querySkuState", List.class, opeDataListener);
            xposedModule.hook(querySkuState).intercept(chain -> {
                Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain));
                return chain.proceed();
            });
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "BiLiBiLi hook error", e);
        }
    }
}
