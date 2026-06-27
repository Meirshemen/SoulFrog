package xmnh.soulfrog.interfaces;

import android.content.Context;

import io.github.libxposed.api.XposedModule;

public interface BaseHook {

    void hook(XposedModule xposedModule, Context context, ClassLoader classLoader);
}
