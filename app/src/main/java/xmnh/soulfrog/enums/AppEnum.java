package xmnh.soulfrog.enums;

public enum AppEnum {
    TIKTOK("TikTok", "com.zhiliaoapp.musically");

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
