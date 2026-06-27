package xmnh.soulfrog.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import xmnh.soulfrog.SoulFrog;

public class HookUtil {
    public static Class<?> findClassIfExists(String className, ClassLoader classLoader) {
        try {
            return Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException e) {
//            Log.e(SoulFrog.TAG, "findClassIfExists error => " + e.getMessage());
            return null;
        }
    }

    public static void replaceReturnValue(XposedModule xposedModule, Method method, Object value) {
        try {
            xposedModule.hook(method).intercept(chain -> value);
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "replaceReturnValue error", e);
        }
    }

    public static void hookAllConstructors(XposedModule xposedModule, Class<?> clazz,
                                           Consumer<XposedInterface.Chain> consumer) {
        try {
            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                xposedModule.hook(constructor)
                        .intercept(chain -> {
                            chain.proceed();
                            consumer.accept(chain);
                            return null;
                        });
            }
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "hookAllConstructors error", e);
        }
    }

    public static void replaceFieldValue(XposedInterface.Chain chain, Class<?> clazz,
                                         String fieldName, Object value) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(chain.getThisObject(), value);
            Log.d(SoulFrog.TAG, "replaceFieldValue: " + fieldName + " " + field.get(chain.getThisObject()));
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "replaceFieldValue error", e);
        }
    }

    public static Object getStaticFieldValue(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "getStaticFieldValue error", e);
        }
        return null;
    }

    public static Object getFieldValue(Class<?> clazz, String fieldName, Object instance) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "getFieldValue error", e);
        }
        return null;
    }

    public static String getMethodSignature(XposedInterface.Chain chain) {
        if (chain == null) return "";
        Object thisObject = chain.getThisObject();
        Class<?>[] paramTypes = chain.getExecutable().getParameterTypes();
        List<Object> args = chain.getArgs();
        StringBuilder sb = new StringBuilder();
        sb.append("\n================================\n");
        sb.append("method: ").append(chain.getExecutable().getName()).append("\n");
        sb.append("args: ").append(paramTypes.length == 0 ? "(No args)\n" : "\n");
        int count = args.size();
        for (int i = 0; i < count; i++) {
            sb.append(" ")
                    .append(paramTypes[i].getName())
                    .append(" = ")
                    .append(args.get(i))
                    .append("\n");
        }
        sb.append("thisObject: ")
                .append(thisObject != null ? thisObject.getClass().getName() : "(No Object)")
                .append("\n================================\n");
        return sb.toString();
    }

    public static String getMethodSignature2(XposedInterface.Chain chain) {
        if (chain == null) return "";
        Object thisObject = chain.getThisObject();
        chain.getExecutable().getParameterTypes();
        List<Object> args = chain.getArgs();
        return "\n================================\n" +
                "method: " + chain.getExecutable().getName() + "\n" +
                "args: " + args + "\n" +
                "thisObject: " + (thisObject != null ? thisObject.getClass().getName() : "null") +
                "\n================================\n";
    }

    public static void hookHttpURLConnectionImpl(XposedModule module, ClassLoader classLoader, String targetUrl,
                                                 Function<XposedInterface.Chain, Object> function) {
        Class<?> httpsURLConnectionImpl =  HookUtil.findClassIfExists(
                "com.android.okhttp.internal.huc.HttpsURLConnectionImpl", classLoader);
        if (httpsURLConnectionImpl == null) return;
        try {
            Method getResponseCode = httpsURLConnectionImpl.getDeclaredMethod("getResponseCode");
            module.hook(getResponseCode).intercept(chain -> {
                HttpURLConnection conn = (HttpURLConnection) chain.getThisObject();
                if (isTargetUrl(conn.getURL().toString(), targetUrl)) {
                    return 200;
                }
                return chain.proceed();
            });
        } catch (NoSuchMethodException e) {
            Log.e(SoulFrog.TAG, "hook getResponseCode failed", e);
        }

        try {
            Method getInputStream = httpsURLConnectionImpl.getDeclaredMethod("getInputStream");
            module.hook(getInputStream).intercept(chain -> {
                HttpURLConnection conn = (HttpURLConnection) chain.getThisObject();
                if (isTargetUrl(conn.getURL().toString(), targetUrl)) {
                    Log.d(SoulFrog.TAG, "Intercepting InputStream for: " + conn.getURL());
                    return function.apply(chain);
                }
                return chain.proceed();
            });
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "hook getInputStream failed", e);
        }

        try {
            Method getErrorStream = httpsURLConnectionImpl.getDeclaredMethod("getErrorStream");
            module.hook(getErrorStream).intercept(chain -> {
                HttpURLConnection conn = (HttpURLConnection) chain.getThisObject();
                if (isTargetUrl(conn.getURL().toString(), targetUrl)) {
                    Log.d(SoulFrog.TAG, "Intercepting getErrorStream for: " + conn.getURL());
                    function.apply(chain);
                }
                return chain.proceed();
            });
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "hook getErrorStream failed", e);
        }
    }

    private static boolean isTargetUrl(String url, String targetUrl) {
        if (url == null) return false;
        return url.contains(targetUrl);
    }


    public static String readStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            Log.e(SoulFrog.TAG, "sha256 error", e);
        }
        return "";
    }

    public static void gsonFromJson(XposedModule xposedModule, ClassLoader classLoader,
                                    Function<XposedInterface.Chain, Object> function) {
        Class<?> gson = findClassIfExists("com.google.gson.Gson", classLoader);
        if (gson == null) return;
        try {
            Method fromJson = gson.getDeclaredMethod("fromJson", String.class, Class.class);
            xposedModule.hook(fromJson).intercept(function::apply);
        } catch (Throwable e) {
            // public <T> T fromJson(String json, Class<T> classOfT)
            for (Method method : gson.getDeclaredMethods()) {
                if (method.getReturnType() == Object.class
                        && method.getParameterTypes().length == 2
                        && method.getParameterTypes()[0] == String.class
                        && method.getParameterTypes()[1] == Class.class) {
                    xposedModule.hook(method).intercept(function::apply);
                }
            }
        }
    }

    public static void gsonToJson(XposedModule xposedModule, ClassLoader classLoader,
                                  Function<XposedInterface.Chain, Object> function) {
        Class<?> gson = findClassIfExists("com.google.gson.Gson", classLoader);
        if (gson == null) return;
        try {
            Method toJson = gson.getDeclaredMethod("toJson", Object.class);
            xposedModule.hook(toJson).intercept(function::apply);
        } catch (Throwable e) {
            for (Method method : gson.getDeclaredMethods()) {
                if (method.getReturnType() == String.class
                        && method.getParameterTypes().length == 1
                        && method.getParameterTypes()[0] == Object.class) {
                    xposedModule.hook(method).intercept(function::apply);
                }
            }
        }
    }

}
