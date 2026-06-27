package xmnh.soulfrog.app;

import android.content.Context;

import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.interfaces.BaseHook;
import xmnh.soulfrog.utils.HookUtil;

public class SdMaidSe implements BaseHook {
    @Override
    public void hook(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        Class<?> info = HookUtil.findClassIfExists("eu.darken.sdmse.common.upgrade.core.UpgradeRepoGplay$Info", classLoader);
        if (info != null) {
            HookUtil.hookAllConstructors(xposedModule, info, chain -> {
                HookUtil.replaceFieldValue(chain, info, "isPro", true);
            });
        }
    }
}
