package xmnh.soulfrog.factory;

import java.util.HashMap;
import java.util.Map;

import xmnh.soulfrog.app.TikTok;
import xmnh.soulfrog.enums.AppEnum;
import xmnh.soulfrog.interfaces.BaseHook;

public class AppFactory {
    private static final Map<String, BaseHook> HOOKS = new HashMap<>();

    static {
        HOOKS.put(AppEnum.TIKTOK.getPackageName(), new TikTok());
    }

    public static BaseHook init(String packageName) {
        return HOOKS.getOrDefault(packageName, null);
    }

}
