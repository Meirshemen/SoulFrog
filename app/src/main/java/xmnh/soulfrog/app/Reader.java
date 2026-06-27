package xmnh.soulfrog.app;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.SoulFrog;
import xmnh.soulfrog.interfaces.BaseHook;
import xmnh.soulfrog.utils.HookUtil;


public class Reader implements BaseHook {

    @Override
    public void hook(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        try {
            context.getSharedPreferences("com.originatorkids.EndlessAlphabet.MainActivity", 0)
                    .edit()
                    .putBoolean("is_purchased_reader_packs_all", true)
                    .apply();
            Class<?> endlessReaderIAPFacade = HookUtil.findClassIfExists("com.originatorkids.EndlessAlphabet.EndlessReaderIAPFacade", classLoader);
            if (endlessReaderIAPFacade != null) {
                Method getBuyAllIAPId = endlessReaderIAPFacade.getDeclaredMethod("getBuyAllIAPId");
                HookUtil.replaceReturnValue(xposedModule, getBuyAllIAPId, "reader_packs_all");
            }
            Class<?> iAPInfo = HookUtil.findClassIfExists("com.originatorkids.psdk.IAPFacade$IAPInfo", classLoader);
            if (iAPInfo != null) {
                HookUtil.hookAllConstructors(xposedModule, iAPInfo, chain -> {
                    HookUtil.replaceFieldValue(chain, iAPInfo, "hasBeenPurchased", true);
                });
            }
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "Reader hook error", e);
        }

    }
}
