package xmnh.soulfrog.game;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.SoulFrog;
import xmnh.soulfrog.utils.HookUtil;

public class Taptap {

    public static void hook(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        try {
            v3(xposedModule, context, classLoader);
            v4(xposedModule, context, classLoader);
            xd(xposedModule, classLoader);
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "hook error", e);
        }
    }

    private static void v3(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        Class<?> tapTapLicense = HookUtil.findClassIfExists("com.taptap.pay.sdk.library.TapTapLicense", classLoader);
        if (tapTapLicense == null) return;
        Log.i(SoulFrog.TAG, "v3 hooked");
        long licenseDate = System.currentTimeMillis() - 430000000L;
        context.getSharedPreferences("tap_license", 0)
                .edit()
                .putLong("last_license_date", licenseDate)
                .putLong("last_license_date_second", licenseDate / 1000)
                .putLong("last_purchased_date", licenseDate)
                .apply();
        try {
            Method check = tapTapLicense.getDeclaredMethod("check", Activity.class, Fragment.class, boolean.class);
            xposedModule.hook(check).intercept(chain -> {
                Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain));
                Object[] args = chain.getArgs().toArray();
                args[2] = false;
                return chain.proceed(args);
            });
            Class<?> tapPurchase = HookUtil.findClassIfExists("com.taptap.pay.sdk.library.TapPurchase", classLoader);
            if (tapPurchase != null) {
                HookUtil.hookAllConstructors(xposedModule, tapPurchase, chain -> {
                    HookUtil.replaceFieldValue(chain, tapPurchase, "isBought", true);
                });
            }
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "v3 error", e);
        }
    }

    private static void v4(XposedModule xposedModule, Context context, ClassLoader classLoader) {
        Class<?> tapLicense = HookUtil.findClassIfExists("com.taptap.sdk.license.TapTapLicense", classLoader);
        if (tapLicense == null) return;
        Log.i(SoulFrog.TAG, "v4 hooked");
        long licenseDate = System.currentTimeMillis() - 430000000L;
        context.getSharedPreferences("tap_sdk_sp", 0)
                .edit()
                .putLong("last_licensed_date", licenseDate)
                .putLong("last_licensed_date_second", licenseDate / 1000)
                .putLong("last_purchased_date", licenseDate)
                .putLong("last_licensed_date_five_days", licenseDate)
                .apply();
        try {
            Method checkLicense = tapLicense.getDeclaredMethod("checkLicense", Activity.class, boolean.class);
            xposedModule.hook(checkLicense).intercept(chain -> {
                Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain));
                Object[] args = chain.getArgs().toArray();
                args[1] = false;
                return chain.proceed(args);
            });
            Class<?> purchaseInfo = HookUtil.findClassIfExists("com.taptap.sdk.license.internal.PurchaseInfo", classLoader);
            if (purchaseInfo != null) {
                HookUtil.hookAllConstructors(xposedModule, purchaseInfo, chain -> {
                    HookUtil.replaceFieldValue(chain, purchaseInfo, "isBought", true);
                });
            }

            Class<?> dlcInventory = classLoader.loadClass("com.taptap.sdk.license.internal.DLCInventory");
            Method hasPurchased = dlcInventory.getDeclaredMethod("hasPurchased", String.class);
            HookUtil.replaceReturnValue(xposedModule, hasPurchased, true);

            Class<?> tapTapLicense = classLoader.loadClass("com.taptap.sdk.license.TapTapLicense");
            Class<?> tapTapDLCCallback = classLoader.loadClass("com.taptap.sdk.license.TapTapDLCCallback");
            Method registerDLCCallback = tapTapLicense.getDeclaredMethod("registerDLCCallback", tapTapDLCCallback);
            xposedModule.hook(registerDLCCallback).intercept(chain -> {
                Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain));
                Class<?> dlcCallback = chain.getArg(0).getClass();
                Method onQueryResult = dlcCallback.getDeclaredMethod("onQueryResult", int.class, HashMap.class);
                xposedModule.hook(onQueryResult).intercept(chain1 -> {
                    Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain1));
                    Object[] args = chain1.getArgs().toArray();
                    args[0] = 0;
                    @SuppressWarnings("unchecked")
                    HashMap<String, Integer> arg1 = (HashMap<String, Integer>) chain1.getArg(1);
                    arg1.replaceAll((key, value) -> 1);
                    args[1] = arg1;
                    return chain1.proceed(args);
                });

                Method onPurchaseResult = dlcCallback.getDeclaredMethod("onPurchaseResult", String.class, int.class);
                xposedModule.hook(onPurchaseResult).intercept(chain1 -> {
                    Log.d(SoulFrog.TAG, HookUtil.getMethodSignature(chain1));
                    Object[] args = chain1.getArgs().toArray();
                    args[1] = 1;
                    return chain1.proceed(args);
                });
                return chain.proceed();
            });
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "v4 error", e);
        }
    }

    private static void xd(XposedModule xposedModule, ClassLoader classLoader) {
        Class<?> xDSDK = HookUtil.findClassIfExists("com.xd.xdsdk.XDSDK", classLoader);
        if (xDSDK == null) return;
        try {
            HookUtil.hookHttpURLConnectionImpl(xposedModule, classLoader, "authorizations/taptap",
                    chain -> {
                        Log.d(SoulFrog.TAG, "authorizations/taptap: " + HookUtil.getMethodSignature(chain));
                        String json = "{\"id\":88888888,\"user_id\":888888888,\"access_token\":\"root\",\"scopes\":\"user,sdk\"}";
                        return new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
                    });
            HookUtil.hookHttpURLConnectionImpl(xposedModule, classLoader, "user?access_token=",
                    chain -> {
                        Log.d(SoulFrog.TAG, "user?access_token=: " + HookUtil.getMethodSignature(chain));
                        String json = "{\"id\":\"888888888\",\"name\":\"SoulFrog\",\"nickname\":SoulFrog,\"friendly_name\":\"root\",\"created\":1666666666,\"last_login\":0,\"site\":\"9\",\"client_id\":\"root\",\"authoriz_state\":1,\"is_upload_play_log\":1,\"id_card\":\"root\",\"adult_type\":4,\"tmp_to_xd\":true,\"safety\":true,\"privacy_agreement\":0,\"fcm\":0,\"anti_addiction_token\":\"root\",\"phone\":\"18888888888\",\"did\":\"a6b6c6d6e6f6-6666-abcd-6666-a6b6c6d6e6f6\",\"ip\":\"223.6.6.6\"}";
                        return new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
                    });
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "xd error", e);
        }
    }

}
