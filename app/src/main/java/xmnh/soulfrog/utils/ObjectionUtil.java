package xmnh.soulfrog.utils;

import android.telephony.TelephonyManager;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class ObjectionUtil {

    public static void ChangeRegion(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(TelephonyManager.class, "getSimCountryIso",
                    XC_MethodReplacement.returnConstant("TW"));
            XposedHelpers.findAndHookMethod(TelephonyManager.class, "getSimOperatorName",
                    XC_MethodReplacement.returnConstant("FarEasTone"));
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
    }

}
