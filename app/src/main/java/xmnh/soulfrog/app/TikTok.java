package xmnh.soulfrog.app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
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

    // מחלקת הראליזציה של ה-Hook עבור מנגנון השמירה המודרני
    public static class InsertHooker implements XposedInterface.Hooker {
        @XposedInterface.Before
        public static void before(XposedInterface.BeforeHookCallback callback) {
            try {
                Uri uri = (Uri) callback.getArgs()[0];
                ContentValues values = (ContentValues) callback.getArgs()[1];
                
                // אם טיקטוק מנסה להכניס סרטון למאגר המדיה של המכשיר
                if (uri != null && uri.toString().contains("video/media")) {
                    if (values != null) {
                        // משנים את נתיב השמירה היחסי מ-Camera ל-Movies/TikTok
                        values.put("relative_path", "Movies/TikTok");
                    }
                }
            } catch (Exception e) {
                Log.e(SoulFrog.TAG, "InsertHooker error", e);
            }
        }
    }

    @Override
    public void hook(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        String TARGET_MCC_MNC = "310260";
        String TARGET_OPERATOR_NAME = "T-Mobile";
        String TARGET_COUNTRY_ISO = "us";
        try {
            // 1. זיוף סים ואזור (עובד פיקס)
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

            // 2. תפיסת מנגנון השמירה המודרני (MediaStore) של אנדרואיד
            Method insertMethod = ContentResolver.class.getDeclaredMethod("insert", Uri.class, ContentValues.class);
            xposedModule.hookMethod(insertMethod, InsertHooker.class);

            // 3. גיבוי למנגנון הישן (למכשירים/גרסאות ישנות יותר)
            Method getExternalStoragePublicDirectory = Environment.class.getDeclaredMethod("getExternalStoragePublicDirectory", String.class);
            File customDir = new File(Environment.getExternalStorageDirectory(), "Movies/TikTok");
            HookUtil.replaceReturnValue(xposedModule, getExternalStoragePublicDirectory, customDir);

        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "TikTok hook error", e);
        }
    }
}