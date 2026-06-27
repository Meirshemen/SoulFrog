package xmnh.soulfrog.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.SoulFrog;
import xmnh.soulfrog.interfaces.BaseHook;
import xmnh.soulfrog.utils.AppUtil;
import xmnh.soulfrog.utils.HookUtil;

public class CraftTheWorld implements BaseHook {

    @Override
    public void hook(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        Class<?> hykbInAppPlugin = HookUtil.findClassIfExists("org.Engine.HykbInAppPlugin", classLoader);
        if (hykbInAppPlugin == null) return;
        try {
            SharedPreferences sp = context.getSharedPreferences(SoulFrog.TAG, Context.MODE_PRIVATE);
            String signAdd = (String) HookUtil.getStaticFieldValue(hykbInAppPlugin, "SignAdd");
            if (signAdd == null) return;
            SharedPreferences.Editor edit = sp.edit();
            String versionName = AppUtil.getAppVersionName(context);
            final String cache = sp.getString(versionName, null);
            HookUtil.hookHttpURLConnectionImpl(xposedModule, classLoader, "ctwcn.3839pic.com/api?data", chain -> {
                if (cache != null) {
                    return new ByteArrayInputStream(cache.getBytes(StandardCharsets.UTF_8));
                }
                edit.clear();
                InputStream result = null;
                try {
                    result = (InputStream) chain.proceed();
                } catch (Throwable e) {
                    Log.d(SoulFrog.TAG, "CraftTheWorld chain.proceed: " + e);
                }
                Log.d(SoulFrog.TAG, "chain result: " + result);
                try {
                    if (result == null) return null;
                    String resultStr = HookUtil.readStreamToString(result);
                    JSONObject resultJson = new JSONObject(resultStr);
                    JSONObject response = resultJson.getJSONObject("response");
                    if (response.has("products") && response.getJSONArray("products").length() > 0) {
                        JSONArray products = response.getJSONArray("products");
                        for (int i = 0; i < products.length(); i++) {
                            JSONObject product = products.getJSONObject(i);
                            product.put("consumable", true);
                        }
                        resultJson.put("signature", HookUtil.sha256(response + signAdd));
                        String resultJsonString = resultJson.toString();
                        edit.putString(versionName, resultJsonString).apply();
                        edit.apply();
                        return new ByteArrayInputStream(resultJsonString.getBytes(StandardCharsets.UTF_8));
                    }
                } catch (Exception e) {
                    Log.d(SoulFrog.TAG, "CraftTheWorld jsonObject: " + e.getMessage());
                    return result;
                }
                return null;
            });
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "CraftTheWorld error", e);
        }
    }


}
