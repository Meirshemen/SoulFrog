package xmnh.soulfrog.enums;

public enum GameEnum {

    CRAFT_THE_WORLD("打造世界", "com.dekovir.CraftTheWorld3839"),
    DEAD_CELLS("重生细胞", "com.bilibili.deadcells.mobile"),
    KING_DOM_5("王国保卫战5", "com.east2west.kingdomrush5.TapTap"),
    // TODO 待填写需要hook的枚举

    ;

    private final String appName;
    private final String packageName;

    GameEnum(String appName, String packageName) {
        this.appName = appName;
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public static GameEnum getAppEnum(String packageName) {
        for (GameEnum appEnum : values()) {
            if (appEnum.getPackageName().equals(packageName)) {
                return appEnum;
            }
        }
        return null;
    }

}
