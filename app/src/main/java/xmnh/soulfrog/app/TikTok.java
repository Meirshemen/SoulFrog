package xmnh.soulfrog.app;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedInterface;
import xmnh.soulfrog.SoulFrog;
import xmnh.soulfrog.interfaces.BaseHook;
import xmnh.soulfrog.utils.HookUtil;


public class TikTok implements BaseHook {
    @Override
    public void hook(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        String TARGET_MCC_MNC = "310260";
        String TARGET_OPERATOR_NAME = "T-Mobile";
        String TARGET_COUNTRY_ISO = "us";
        try {
            // --- קוד קיים: מעקף הגבלות אזוריות ---
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

            // --- תכונה חדשה: שינוי נתיב הורדת סרטונים ל- Movies/TikTok ---
            Method getPublicDir = Environment.class.getDeclaredMethod("getExternalStoragePublicDirectory", String.class);
            
            xposedModule.hookMethod(getPublicDir, new XposedInterface.Hooker() {
                @Override
                public void after(XposedInterface.AfterHookCallback callback) throws Throwable {
                    String type = (String) callback.getArgs()[0];
                    
                    // בודק אם טיקטוק מבקשת את תיקיית הסרטים (Movies) או תיקיית המצלמה הכללית (DCIM)
                    if (Environment.DIRECTORY_MOVIES.equals(type) || Environment.DIRECTORY_DCIM.equals(type)) {
                        File originalDir = (File) callback.getResult();
                        if (originalDir != null) {
                            // יוצר נתיב חדש: Movies/TikTok
                            File tiktokDir = new File(originalDir, "TikTok");
                            if (!tiktokDir.exists()) {
                                tiktokDir.mkdirs(); // יצירת התיקייה במכשיר במידה והיא לא קיימת
                            }
                            callback.setResult(tiktokDir); // החלפת התוצאה שהאפליקציה תקבל
                        }
                    }
                }
            });

        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "TikTok hook error", e);
        }
    }
}
