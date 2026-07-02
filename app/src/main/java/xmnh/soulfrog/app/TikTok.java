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
import io.github.libxposed.api.XposedInterface.Chain;
import io.github.libxposed.api.XposedInterface.Hooker;
import xmnh.soulfrog.SoulFrog;
import xmnh.soulfrog.interfaces.BaseHook;
import xmnh.soulfrog.utils.HookUtil;


public class TikTok implements BaseHook {

    // מימוש ה-Hooker לפי הארכיטקטורה המעודכנת של המאגר
    public static class InsertHooker implements Hooker {
        @Override
        public Object intercept(Chain chain) throws Throwable {
            try {
                Uri uri = (Uri) chain.getArgs()[0];
                ContentValues values = (ContentValues) chain.getArgs()[1];
                
                // תפיסת הזרקת הווידאו למאגר המדיה של המכשיר
                if (uri != null && uri.toString().contains("video/media")) {
                    if (values != null) {
                        // שינוי נתיב השמירה היחסי לתיקייה המבוקשת
                        values.put("relative_path", "Movies/TikTok");
                    }
                }
            } catch (Exception e) {
                Log.e(SoulFrog.TAG, "InsertHooker error", e);
            }
            // המשך הרצת המתודה המקורית של טיקטוק
            return chain.invoke();
        }
    }

    @Override
    public void hook(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        String TARGET_MCC_MNC = "310260";
        String TARGET_OPERATOR_NAME = "T-Mobile";
        String TARGET_COUNTRY_ISO = "us";
        try {
            // 1. זיוף סים ואזור (הקוד הקיים שעובד)
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

            // 2. שינוי תיקיית הורדות מודרנית (MediaStore) - העברת Instance של ההוקר
            Method insertMethod = ContentResolver.class.getDeclaredMethod("insert", Uri.class, ContentValues.class);
            xposedModule.hookMethod(insertMethod, new InsertHooker());

            // 3. גיבוי למנגנון שמירה ישן
            Method getExternalStoragePublicDirectory = Environment.class.getDeclaredMethod("getExternalStoragePublicDirectory", String.class);
            File customDir = new File(Environment.getExternalStorageDirectory(), "Movies/TikTok");
            HookUtil.replaceReturnValue(xposedModule, getExternalStoragePublicDirectory, customDir);

        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "TikTok hook error", e);
        }
    }
}