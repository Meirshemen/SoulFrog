package xmnh.soulfrog.app;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.SoulFrog;
import xmnh.soulfrog.interfaces.BaseHook;
import xmnh.soulfrog.utils.HookUtil;


public class QQLive implements BaseHook {

    @Override
    public void hook(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        try {
            splash(xposedModule, classLoader);
            watermark(xposedModule, classLoader);
            pause(xposedModule, classLoader);
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "QQLive hook error", e);
        }
    }

    private void splash(XposedModule xposedModule, ClassLoader classLoader) {
        try {
            Method getBooleanExtra = Intent.class.getDeclaredMethod("getBooleanExtra",
                    String.class, boolean.class);
            xposedModule.hook(getBooleanExtra).intercept(chain -> {
                Object[] args = chain.getArgs().toArray();
                String args0 = (String) args[0];
                if ("home_need_splash".equals(args0) || "showSplashAd".equals(args0)) {
                    args[1] = false;
                }
                return chain.proceed(args);
            });
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "splash error", e);
        }
    }

    private void watermark(XposedModule xposedModule, ClassLoader classLoader) {
        Class<?> tVKWatermarkImageView = HookUtil.findClassIfExists(
                "com.tencent.qqlive.tvkplayer.oal.view.subview.TVKWatermarkImageView", classLoader);
        if (tVKWatermarkImageView == null) return;
        try {
            Method setViewVisible = tVKWatermarkImageView.getDeclaredMethod("setViewVisible", boolean.class);
            xposedModule.hook(setViewVisible).intercept(chain -> {
                Object[] args = chain.getArgs().toArray();
                args[0] = false;
                return chain.proceed(args);
            });
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "watermark error", e);
        }
    }

    private void pause(XposedModule xposedModule, ClassLoader classLoader) {
        Class<?> tVKPlayerManager = HookUtil.findClassIfExists(
                "com.tencent.qqlive.tvkplayer.logic.TVKPlayerManager", classLoader);
        if (tVKPlayerManager == null) return;
        try {
            Method pauseWithIsAllowShowPauseAd = tVKPlayerManager.getDeclaredMethod("pauseWithIsAllowShowPauseAd",
                    boolean.class, ViewGroup.class);
            xposedModule.hook(pauseWithIsAllowShowPauseAd).intercept(chain -> {
                Object[] args = chain.getArgs().toArray();
                args[0] = false;
                return chain.proceed(args);
            });
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "pause error", e);
        }
    }

}
