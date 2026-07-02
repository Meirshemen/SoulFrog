package xmnh.soulfrog.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import xmnh.soulfrog.application.SoulFrog;

public class AppUtil {
    private static Toast toast;

    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), Context.MODE_PRIVATE)
                    .versionName;
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "getAppVersionName error => " + e);
        }
        return "";
    }

    public static void finish(Context context) {
        String appName = getAppName(context);
        String text = SoulFrog.TAG + " => " + appName + " : " + "~ start running ~";
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }

    private static String getAppName(Context context) {
        String packageName = context.getPackageName();
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, Context.MODE_PRIVATE);
            if (packageInfo.applicationInfo == null) return "";
            return packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(SoulFrog.TAG, "getAppName error => " + e);
        }
        return "";
    }

}
