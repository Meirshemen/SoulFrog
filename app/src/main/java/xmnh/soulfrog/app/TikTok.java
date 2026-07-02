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
            // 1. זיוף נתוני רשת וסים (הקוד המקורי הקיים)
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

            // ==========================================
            // 2. תפיסת מנגנון השמירה המודרני (MediaStore) עם התחביר המדויק של הפרויקט
            // ==========================================
            Method insertMethod = ContentResolver.class.getDeclaredMethod("insert", Uri.class, ContentValues.class);
            xposedModule.hook(insertMethod).intercept(chain -> {
                try {
                    Uri uri = (Uri) chain.getArg(0);
                    ContentValues values = (ContentValues) chain.getArg(1);
                    
                    // ניתוב מחדש של השמירה לתיקייה המותאמת אישית
                    if (uri != null && uri.toString().contains("video/media")) {
                        if (values != null) {
                            values.put("relative_path", "Movies/TikTok");
                        }
                    }
                } catch (Exception e) {
                    Log.e(SoulFrog.TAG, "Insert hook error", e);
                }
                return chain.proceed(); // המשך ריצה טבעית עם הערכים המעודכנים
            });

            // ==========================================
            // 3. גיבוי למנגנון השמירה הישן - בדיקה כירורגית כדי לא לדרוס תיקיות אחרות
            // ==========================================
            Method getExternalStoragePublicDirectory = Environment.class.getDeclaredMethod("getExternalStoragePublicDirectory", String.class);
            xposedModule.hook(getExternalStoragePublicDirectory).intercept(chain -> {
                String type = (String) chain.getArg(0);
                // רק אם האפליקציה מבקשת במפורש את תיקיית המצלמה או הסרטים
                if (Environment.DIRECTORY_DCIM.equals(type) || Environment.DIRECTORY_MOVIES.equals(type)) {
                    return new File(Environment.getExternalStorageDirectory(), "Movies/TikTok");
                }
                return chain.proceed();
            });

        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "TikTok hook error", e);
        }
    }
}