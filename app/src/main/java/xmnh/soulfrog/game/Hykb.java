package xmnh.soulfrog.game;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.SoulFrog;
import xmnh.soulfrog.utils.HookUtil;

public class Hykb {

    public static void hook(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        try {
            hookPaidChecker(xposedModule, classLoader);
            hookDLC(xposedModule, classLoader);
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "hykb hook error", e);
        }
    }

    private static void hookPaidChecker(XposedModule xposedModule, ClassLoader classLoader) {
        Class<?> hykbPaidChecker = HookUtil.findClassIfExists("com.m3839.sdk.paid.HykbPaidChecker", classLoader);
        if (hykbPaidChecker == null) return;
        try {
            for (Method declaredMethod : hykbPaidChecker.getDeclaredMethods()) {
                if ("checkLicense".equals(declaredMethod.getName())) {
                    xposedModule.hook(declaredMethod).intercept(chain -> {
                        Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain));
                        Object listener = chain.getArg(chain.getArgs().size() - 1);
                        Method onAllowEnter = listener.getClass().getMethod("onAllowEnter");
                        onAllowEnter.invoke(listener);
                        return null;
                    });
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "hook checkLicense failed", e);
        }
    }

    private static void hookDLC(XposedModule xposedModule, ClassLoader classLoader) {
        Class<?> hykbDLC = HookUtil.findClassIfExists("com.m3839.sdk.dlc.HykbDLC", classLoader);
        if (hykbDLC == null) return;
        try {
            Class<?> queryListener = classLoader.loadClass("com.m3839.sdk.dlc.listener.HykbDLCQueryListener");
            Method queryMethod = hykbDLC.getDeclaredMethod("query", Activity.class, int.class, queryListener);
            xposedModule.hook(queryMethod).intercept(chain -> {
                Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain));
                Object arg2 = chain.getArg(2);
                Method onSucceed = arg2.getClass().getDeclaredMethod("onSucceed", int.class);
                xposedModule.hook(onSucceed).intercept(innerChain -> {
                    Object[] newArgs = innerChain.getArgs().toArray();
                    newArgs[0] = 1;
                    return innerChain.proceed(newArgs);
                });
                return chain.proceed();
            });
            Class<?> queryAllListener = classLoader.loadClass("com.m3839.sdk.dlc.listener.HykbDLCQueryAllListener");
            Method queryAllMethod = hykbDLC.getDeclaredMethod("queryAll", Activity.class, queryAllListener);
            xposedModule.hook(queryAllMethod).intercept(chain -> {
                Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain));
                Object arg1 = chain.getArg(1);
                Class<?> statusDataCls = classLoader.loadClass("com.m3839.sdk.dlc.bean.HykbDLCSkuStatusData");
                Method onSucceed = arg1.getClass().getDeclaredMethod("onSucceed", statusDataCls);
                xposedModule.hook(onSucceed).intercept(innerChain -> {
                    Object args = innerChain.getArg(0);
                    Field listField = args.getClass().getDeclaredField("list");
                    listField.setAccessible(true);
                    List<?> list = (List<?>) listField.get(args);
                    for (Object obj : list) {
                        Field statusField = obj.getClass().getDeclaredField("status");
                        statusField.setAccessible(true);
                        statusField.setInt(obj, 1);
                    }
                    Log.d(SoulFrog.TAG, "queryAll listener args => " + args);
                    return innerChain.proceed();
                });
                return chain.proceed();
            });
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "hykb error", e);
        }
    }

}
