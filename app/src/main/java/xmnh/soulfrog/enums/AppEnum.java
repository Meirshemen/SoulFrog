package xmnh.soulfrog.enums;

public enum AppEnum {
    QQ_LIVE("腾讯视频", "com.tencent.qqlive"),
    BRAIN_WAVE("神奇脑波", "imoblife.brainwavestus"),
    RR_VIDEO("人人视频", "com.example.pptv"),
    ES_BROWSE("ES文件浏览器", "com.estrongs.android.pop"),
    SD_MAID_SE("SdMaidSE", "eu.darken.sdmse"),
    READER("Reader", "com.originatorkids.EndlessReader"),
    TIKTOK("TikTok", "com.zhiliaoapp.musically"),
    // TODO 待填写需要hook的枚举

    ;

    private final String appName;
    private final String packageName;

    AppEnum(String appName, String packageName) {
        this.appName = appName;
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public static AppEnum getAppEnum(String packageName) {
        for (AppEnum appEnum : values()) {
            if (appEnum.getPackageName().equals(packageName)) {
                return appEnum;
            }
        }
        return null;
    }

}
