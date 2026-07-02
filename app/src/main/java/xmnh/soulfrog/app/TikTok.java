package xmnh.soulfrog.app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedInterface.BeforeHookCallback;
import io.github.libxposed.api.annotations.Before;
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
            // 1. זיוף סים ואזור (באמצעות מחלקת העזר של המפתח)
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
            // 2. שינוי תיקיית הורדות ל-Movies/TikTok (התאמה ל-LibXposed)
            // ==========================================
            
            // מנגנון שמירה מודרני (אנדרואיד 10 ומעלה)
            Method insertMethod = ContentResolver.class.getDeclaredMethod("insert", Uri.class, ContentValues.class);
            xposedModule.hookMethod(insertMethod, new Object() {
                @Before
                public void before(BeforeHookCallback callback) {
                    Uri uri = (Uri) callback.getArgs()[0];
                    ContentValues values = (ContentValues) callback.getArgs()[1];
                    
                    if (uri != null && uri.toString().contains("video/media")) {
                        if (values != null && values.containsKey(MediaStore.Video.Media.RELATIVE_PATH)) {
                            values.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/TikTok");
                        }
                    }
                }
            });

            // מנגנון שמירה ישן (לגיבוי)
            Method getExternalStoragePublicDirectory = Environment.class.getDeclaredMethod("getExternalStoragePublicDirectory", String.class);
            xposedModule.hookMethod(getExternalStoragePublicDirectory, new Object() {
                @Before
                public void before(BeforeHookCallback callback) {
                    String type = (String) callback.getArgs()[0];
                    if (Environment.DIRECTORY_DCIM.equals(type) || Environment.DIRECTORY_MOVIES.equals(type)) {
                        File customDir = new File(Environment.getExternalStorageDirectory(), "Movies/TikTok");
                        callback.setResult(customDir);
                    }
                }
            });

        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "TikTok hook error", e);
        }
    }
}