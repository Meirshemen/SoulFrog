package xmnh.soulfrog;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.function.BiConsumer;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import xmnh.soulfrog.app.TikTok;
import xmnh.soulfrog.context.XpContext;
import xmnh.soulfrog.interfaces.BaseHook;

public class Jump implements IXposedHookLoadPackage {

    private void hookAttach(BiConsumer<Context, ClassLoader> consumer) {
        XposedHelpers.findAndHookMethod(Application.class, "attach",
                Context.class,
                new XC_MethodHook() {
                    public void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = XpContext.getContext((Context) param.args[0]);
                        try {
                            XpContext.classLoader = context.getClassLoader();
                        } catch (XposedHelpers.ClassNotFoundError e) {
                            XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
                                public void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                                    Class<?> result = (Class<?>) param.getResult();
                                    Log.d("SoulFrog", "loadClass => " + result);
                                    if (result == null) {
                                        return;
                                    }
                                    XpContext.classLoader = result.getClassLoader();
                                }
                            });
                        } finally {
                            if (XpContext.classLoader != null) {
                                consumer.accept(XpContext.getContext(context), XpContext.classLoader);
                            }
                        }
                    }
                });
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam param) throws Throwable {
        try {
            BaseHook tiktokHook = new TikTok();
            hookAttach((context, classLoader) -> {
                if (tiktokHook != null) {
                    tiktokHook.hook(context, classLoader);
                }
            });

        } catch (Throwable e) {
            XposedBridge.log(e);
        }
    }

}
