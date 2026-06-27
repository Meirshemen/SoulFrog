package xmnh.soulfrog.factory;

import java.util.HashMap;
import java.util.Map;

import xmnh.soulfrog.enums.GameEnum;
import xmnh.soulfrog.game.CraftTheWorld;
import xmnh.soulfrog.game.DeadCells;
import xmnh.soulfrog.game.Kingdom5;
import xmnh.soulfrog.interfaces.BaseHook;


public class GameFactory {
    private static final Map<String, BaseHook> HOOKS = new HashMap<>();

    static {
        HOOKS.put(GameEnum.CRAFT_THE_WORLD.getPackageName(), new CraftTheWorld());
        HOOKS.put(GameEnum.DEAD_CELLS.getPackageName(), new DeadCells());
        HOOKS.put(GameEnum.KING_DOM_5.getPackageName(), new Kingdom5());
        // TODO 待填写需要hook的实例
    }

    public static BaseHook init(String packageName) {
        return HOOKS.getOrDefault(packageName, null);
    }

}
