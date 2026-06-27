package xmnh.soulfrog.utils;


import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import xmnh.soulfrog.SoulFrog;

public class CheckUtil {

    private static final String HOOK_CONFIG_URL = "https://gitee.com/api/v5/repos/ximengnaihe/hook/contents/hook.json";
    private static final int TIMEOUT_MS = 3000;
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static void queryHookEnable(Callback callback) {
        EXECUTOR.execute(() -> {
            HttpURLConnection conn = null;
            try {
                conn = openConnection(HOOK_CONFIG_URL);
                String rawJson = readResponse(conn);
                JSONObject hookJson = parseHookJson(rawJson);
                callback.onSuccess(hookJson);
            } catch (Exception e) {
                Log.e(SoulFrog.TAG, "queryHookEnable error", e);
                callback.onError(e);
            } finally {
                if (conn != null) conn.disconnect();
            }
        });
    }

    private static HttpURLConnection openConnection(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        int code = conn.getResponseCode();
        Log.d(SoulFrog.TAG, "Status Code: " + code);
        if (code != HttpURLConnection.HTTP_OK) {
            throw new IOException("Unexpected HTTP status: " + code);
        }
        return conn;
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
    }

    private static JSONObject parseHookJson(String rawJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(rawJson);
        String content = jsonObject.getString("content");
        byte[] decoded = Base64.decode(content, Base64.DEFAULT);
        String json = new String(decoded, StandardCharsets.UTF_8);
        return new JSONObject(json);
    }

    public interface Callback {
        void onSuccess(JSONObject jsonObject);

        void onError(Exception e);
    }

}

