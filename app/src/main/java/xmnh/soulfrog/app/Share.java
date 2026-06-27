package xmnh.soulfrog.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.SoulFrog;
import xmnh.soulfrog.utils.HookUtil;

public class Share {

    public static void qqShare(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        Class<?> tencent = HookUtil.findClassIfExists("com.tencent.tauth.Tencent", classLoader);
        if (tencent == null) return;
        try {
            Method shareToQQ = tencent.getDeclaredMethod("shareToQQ", Activity.class,
                    Bundle.class,
                    classLoader.loadClass("com.tencent.tauth.IUiListener"));
            xposedModule.hook(shareToQQ).intercept(chain -> {
                Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain));
                Object listener = chain.getArg(2);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("ret", 0);
                    Method onComplete = listener.getClass().getMethod("onComplete", Object.class);
                    onComplete.invoke(listener, jsonObject);
                } catch (Exception e) {
                    Log.e(SoulFrog.TAG, "qqShare onComplete invoke error", e);
                }
                return null;
            });

        } catch (Throwable e) {
            Log.d(SoulFrog.TAG, "qqShare error => " + e);
        }
    }
}