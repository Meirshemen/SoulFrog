package xmnh.soulfrog.app;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.SoulFrog;
import xmnh.soulfrog.interfaces.BaseHook;
import xmnh.soulfrog.utils.HookUtil;


public class EsBrowse implements BaseHook {

    @Override
    public void hook(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        Class<?> accountInfo = HookUtil.findClassIfExists("com.estrongs.android.pop.app.account.model.AccountInfo", classLoader);
        try {
            HookUtil.gsonFromJson(xposedModule, classLoader, chain -> {
                Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain));
                if (accountInfo != null && chain.getArg(1) == accountInfo) {
                    try {
                        String jsonStr = (String) chain.getArg(0);
                        JSONObject jsonObject = new JSONObject(jsonStr);
                        if (jsonObject.has("isVip") && jsonObject.has("vipFinishAt")) {
                            jsonObject.put("isVip", true);
                            jsonObject.put("vipFinishAt", 218330035688000L);
                            Object[] args = chain.getArgs().toArray();
                            args[0] = jsonObject.toString();
                            return chain.proceed(args);
                        }
                    } catch (Throwable e) {
                        Log.e(SoulFrog.TAG, "EsBrowse hook json error", e);
                    }
                }
                try {
                    return chain.proceed();
                } catch (Throwable e) {
                    Log.e(SoulFrog.TAG, "EsBrowse hook error", e);
                    return null;
                }
            });
        } catch (Throwable e) {
            Log.e(SoulFrog.TAG, "EsBrowse hook error", e);
        }
    }

}
