package xmnh.soulfrog.factory;

import java.util.HashMap;
import java.util.Map;

import xmnh.soulfrog.app.BrainWave;
import xmnh.soulfrog.app.EsBrowse;
import xmnh.soulfrog.app.QQLive;
import xmnh.soulfrog.app.RRVideo;
import xmnh.soulfrog.app.Reader;
import xmnh.soulfrog.app.SdMaidSe;
import xmnh.soulfrog.app.TikTok;
import xmnh.soulfrog.enums.AppEnum;
import xmnh.soulfrog.interfaces.BaseHook;


public class AppFactory {
    private static final Map<String, BaseHook> HOOKS = new HashMap<>();

    static {
        HOOKS.put(AppEnum.READER.getPackageName(), new Reader());
        HOOKS.put(AppEnum.ES_BROWSE.getPackageName(), new EsBrowse());
        HOOKS.put(AppEnum.SD_MAID_SE.getPackageName(), new SdMaidSe());
        HOOKS.put(AppEnum.BRAIN_WAVE.getPackageName(), new BrainWave());
        HOOKS.put(AppEnum.RR_VIDEO.getPackageName(), new RRVideo());
        HOOKS.put(AppEnum.QQ_LIVE.getPackageName(), new QQLive());
        HOOKS.put(AppEnum.TIKTOK.getPackageName(), new TikTok());
        // TODO 待填写需要hook的实例
    }

    public static BaseHook init(String packageName) {
        return HOOKS.getOrDefault(packageName, null);
    }

}
