package xmnh.soulfrog.game;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.SoulFrog;
import xmnh.soulfrog.interfaces.BaseHook;
import xmnh.soulfrog.utils.HookUtil;


public class DeadCells implements BaseHook {

    @Override
    public void hook(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        Class<?> thirdPartySDK = HookUtil.findClassIfExists("com.playdigious.deadcells.mobile.ThirdPartySDK", classLoader);
        if (thirdPartySDK == null) return;
        try {
            Method doesOwnDLC = thirdPartySDK.getDeclaredMethod("doesOwnDLC", String.class);
            HookUtil.replaceReturnValue(xposedModule, doesOwnDLC, true);

            Method doesOwnGame = thirdPartySDK.getDeclaredMethod("doesOwnGame");
            HookUtil.replaceReturnValue(xposedModule, doesOwnGame, true);
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "DEAD_CELLS hook error", e);
        }
    }
}
