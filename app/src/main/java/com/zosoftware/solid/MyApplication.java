package com.zosoftware.solid;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import com.zosoftware.solid.api.Api;
import com.zosoftware.solid.utils.TTSUtils;
import com.zosoftware.solid.utils.MqttManager;


public class MyApplication extends Application {
    private static Activity currentActivity;
    private static Context currentContext;
    private static MqttManager mqttManager;
    private static String clientId;
    @Override
    public void onCreate() {
        super.onCreate();
        // 延迟初始化TTS（解决启动过早问题）
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            TTSUtils.initTTS(MyApplication.this);
        }, 2000); // 延迟2秒初始化
        if (clientId == null) {
            clientId = generateRandomClientId();
        }
        clientId = getAndroidId();
        clientId = generateRandomClientId();
        mqttManager = MqttManager.getInstance(
                "tcp://47.109.196.210:1883",
                "admin1",
                "admin1",
                clientId,
                "test/1",
                "test/ble"
        );
        mqttManager.initMqttClient(getApplicationContext());
    }
    // 获取 Android ID
    private String getAndroidId() {
        ContentResolver contentResolver = getContentResolver();
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
    }
    private String generateRandomClientId() {
        String prefix = "mqttx_";
        String characters = "abcdefghijklmnopqrstuvwxyz"; // 字符集
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return prefix + sb;
    }
    public static MqttManager getMqttManager() {
        return mqttManager;
    }
    public static void setCurrentActivityContext(Activity activity, Context context) {
        currentActivity = activity;
        currentContext = context;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static Context getCurrentContext() {
        return currentContext;
    }

}

