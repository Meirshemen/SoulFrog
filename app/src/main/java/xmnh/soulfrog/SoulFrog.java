package xmnh.soulfrog;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;

import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.app.Share;
import xmnh.soulfrog.factory.AppFactory;
import xmnh.soulfrog.factory.GameFactory;
import xmnh.soulfrog.game.Hykb;
import xmnh.soulfrog.game.Taptap;
import xmnh.soulfrog.interfaces.BaseHook;
import xmnh.soulfrog.utils.AppUtil;
import xmnh.soulfrog.utils.CheckUtil;

public class SoulFrog extends XposedModule {
    public static final String TAG = "SoulFrog";

    private void hookAttach(BiConsumer<Context, ClassLoader> consumer) {
        try {
            @SuppressLint("DiscouragedPrivateApi")
            Method attach = Application.class.getDeclaredMethod("attach", Context.class);
            hook(attach).intercept(chain -> {
                Context context = (Context) chain.getArg(0);
                ClassLoader classLoader = context.getClassLoader();
                Object result = chain.proceed();
                consumer.accept(context, classLoader);
                return result;
            });
        } catch (Exception e) {
            Log.e(TAG, "hookAttach failed", e);
        }
    }

    @Override
    public void onPackageReady(PackageReadyParam param) {
        String packageName = param.getPackageName();
        Log.i(TAG, "onPackageReady: " + packageName);
        BaseHook app = AppFactory.init(packageName);
        BaseHook game = GameFactory.init(packageName);
        try {
            hookAttach((context, classLoader) -> {
                AppUtil.toast(context);
                Share.qqShare(this, context, classLoader);
                if (app != null) {
                    app.hook(this, context, classLoader);
                    return;
                }
                if (game != null) {
                    game.hook(this, context, classLoader);
                }
                Log.i(TAG, "app and game factory is null");
                CheckUtil.queryHookEnable(new CheckUtil.Callback() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        if (jsonObject == null) return;
                        Log.i(TAG, "configJson: " + jsonObject);
                        int taptap = jsonObject.optInt("taptap", 0);
                        if (taptap == 1) {
                            Taptap.hook(SoulFrog.this, context, classLoader);
                        }
                        int hykb = jsonObject.optInt("hykb", 0);
                        if (hykb == 1) {
                            Hykb.hook(SoulFrog.this, context, classLoader);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "queryHookEnable error", e);
                    }
                });
            });
        } catch (Throwable e) {
            log(Log.ERROR, TAG, "hook error", e);
            Log.e(TAG, "hook error", e);
        }
    }
}
