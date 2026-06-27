package xmnh.soulfrog.game;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.SoulFrog;
import xmnh.soulfrog.interfaces.BaseHook;
import xmnh.soulfrog.utils.AppUtil;
import xmnh.soulfrog.utils.HookUtil;

public class Kingdom5 implements BaseHook {

    @Override
    public void hook(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        try {
            hookAliPay(xposedModule, classLoader);
            hookWxPay(xposedModule, classLoader);
            AppUtil.toast(context, "hook内购成功");
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "KING_DOM_5 hook error", e);
        }
    }

    private void hookAliPay(XposedModule xposedModule, ClassLoader classLoader) {
        Class<?> alipay = HookUtil.findClassIfExists("com.east2west.alipay.Alipay", classLoader);
        if (alipay == null) return;
        try {
            Method startAliPay = alipay.getDeclaredMethod("startAliPay",
                    String.class, classLoader.loadClass("com.east2west.alipay.Pay$AliPayListener"));
            xposedModule.hook(startAliPay).intercept(chain -> {
                Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain));
                Object[] args = chain.getArgs().toArray();
                toSuccess(xposedModule, args[1]);
                return chain.proceed();
            });
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "KING_DOM_5 hookAliPay error", e);
        }
    }

    private void hookWxPay(XposedModule xposedModule, ClassLoader classLoader) {
        Class<?> wxPay = HookUtil.findClassIfExists("com.east2west.wxpay.WeiXinPay", classLoader);
        if (wxPay == null) return;
        try {
            Method startWXPay = wxPay.getDeclaredMethod("startWXPay",
                    String.class, String.class, String.class,
                    String.class, String.class, String.class,
                    classLoader.loadClass("com.east2west.wxpay.Pay$WxPayListener"));
            xposedModule.hook(startWXPay).intercept(chain -> {
                Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain));
                Object[] args = chain.getArgs().toArray();
                toSuccess(xposedModule, args[6]);
                return chain.proceed();
            });
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "KING_DOM_5 hookWxPay error", e);
        }
    }

    private void toSuccess(XposedModule xposedModule, Object listener) {
        if (listener == null) return;
        Class<?> listenerClass = listener.getClass();
        try {
            Method onPayCancel = listenerClass.getDeclaredMethod("onPayCancel");
            Method onPaySuccess = listenerClass.getDeclaredMethod("onPaySuccess");
            xposedModule.hook(onPayCancel).intercept(chain -> {
                Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain));
                onPaySuccess.invoke(chain.getThisObject());
                return null;
            });
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "toSuccess error", e);
        }
    }

}
