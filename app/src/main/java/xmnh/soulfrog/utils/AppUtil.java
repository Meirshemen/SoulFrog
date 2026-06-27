package xmnh.soulfrog.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import xmnh.soulfrog.SoulFrog;

public class AppUtil {

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

    public static long getAppVersionCode(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), Context.MODE_PRIVATE)
                    .getLongVersionCode();
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "getAppVersionCode error => " + e);
        }
        return 0;
    }

    public static String getAppName(Context context) {
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

    public static String getDefaultSpName(Context context) {
        return context.getPackageName() + "_preferences";
    }

    public static void toast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    public static void toast(Context context) {
        String appName = getAppName(context);
        String text = SoulFrog.TAG + " => " + appName + " : " + "~ start running ~";
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void resetDataDir(Context context, String packageName) {
        if (packageName == null || packageName.isEmpty()) return;
        File dataDir = context.getDataDir();
        File filesDir = context.getFilesDir();
        if (packageName.equals(context.getPackageName())) {
            deleteDirectory(dataDir);
            deleteDirectory(filesDir);
        }
    }

    private static void deleteDirectory(File file) {
        if (!file.exists()) return;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    deleteDirectory(subFile);
                }
            }
        }
        boolean delete = file.delete();
        Log.d(SoulFrog.TAG, delete ? "删除成功" : "删除失败");
    }

}
