package xmnh.soulfrog.app;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.SoulFrog;
import xmnh.soulfrog.interfaces.BaseHook;
import xmnh.soulfrog.utils.AppUtil;
import xmnh.soulfrog.utils.HookUtil;

public class BrainWave implements BaseHook {

    @Override
    public void hook(XposedModule xposedModule,Context context, ClassLoader classLoader) {
        String defaultSpName = AppUtil.getDefaultSpName(context);
        context.getSharedPreferences(defaultSpName, Context.MODE_PRIVATE)
                .edit()
                .putBoolean("is_svip", true)
                .apply();
        try {
            Class<?> account = HookUtil.findClassIfExists("com.imoblife.brainwave.storge.Account", classLoader);
            if (account != null) {
                Class<?> continuation = classLoader.loadClass("kotlin.coroutines.Continuation");
                Method isSuperPackageUser = account.getDeclaredMethod("isSuperPackageUser", continuation);
                HookUtil.replaceReturnValue(xposedModule, isSuperPackageUser, true);
            }
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "BrainWave hook error", e);
        }

    }

}
