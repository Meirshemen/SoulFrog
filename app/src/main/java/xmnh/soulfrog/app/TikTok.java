package xmnh.soulfrog.app;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.SoulFrog;
import xmnh.soulfrog.interfaces.BaseHook;
import xmnh.soulfrog.utils.HookUtil;


public class TikTok implements BaseHook {
    @Override
    public void hook(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        String TARGET_MCC_MNC = "310260";
        String TARGET_OPERATOR_NAME = "T-Mobile";
        String TARGET_COUNTRY_ISO = "us";
        try{
            Method getSimOperator = TelephonyManager.class.getDeclaredMethod("getSimOperator");
            HookUtil.replaceReturnValue(xposedModule, getSimOperator, TARGET_MCC_MNC);
            Method getSimOperatorName = TelephonyManager.class.getDeclaredMethod("getSimOperatorName");
            HookUtil.replaceReturnValue(xposedModule, getSimOperatorName, TARGET_OPERATOR_NAME);
            Method getSimCountryIso = TelephonyManager.class.getDeclaredMethod("getSimCountryIso");
            HookUtil.replaceReturnValue(xposedModule, getSimCountryIso, TARGET_COUNTRY_ISO);
            Method getNetworkOperator = TelephonyManager.class.getDeclaredMethod("getNetworkOperator");
            HookUtil.replaceReturnValue(xposedModule, getNetworkOperator, TARGET_MCC_MNC);
            Method getNetworkOperatorName = TelephonyManager.class.getDeclaredMethod("getNetworkOperatorName");
            HookUtil.replaceReturnValue(xposedModule, getNetworkOperatorName, TARGET_OPERATOR_NAME);
            Method getNetworkCountryIso = TelephonyManager.class.getDeclaredMethod("getNetworkCountryIso");
            HookUtil.replaceReturnValue(xposedModule, getNetworkCountryIso, TARGET_COUNTRY_ISO);
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "TikTok hook error", e);
        }
    }
}
