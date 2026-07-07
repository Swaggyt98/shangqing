package com.zosoftware.solid.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// 为防止各参数报警短时内相互冲突，AlertManager对象收集当前5秒内产生的所有报警信息，统一播报，周而复始
public class AlertManager {

    private static final String TAG = "监测异常报警";
    private static AlertManager instance;
    private static final long DEBOUNCE_TIME_MS = 5000; // 5秒的延迟

    private final List<String> activeAlerts = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isReporting = false;

    private AlertManager() {}

    // 获取单例实例
    public static synchronized AlertManager getInstance() {
        if (instance == null) {
            instance = new AlertManager();
        }
        return instance;
    }

    // 添加一个报警信息
    public synchronized void addAlert(String alertMessage) {
        activeAlerts.add(alertMessage);
        Log.d(TAG, "添加报警信息: " + alertMessage);
        // 如果当前没有正在报告，开始定时器
        if (!isReporting) {
            isReporting = true;
            handler.postDelayed(this::reportAlerts, DEBOUNCE_TIME_MS);
        }
    }


    // 定时器到时后，合并并播报所有报警信息
    private synchronized void reportAlerts() {
        if (activeAlerts.isEmpty()) {
            isReporting = false;
            return;
        }
        // 合并报警信息
        StringBuilder combinedMessage = new StringBuilder();
        for (String alert : activeAlerts) {
            combinedMessage.append(alert).append("，");
        }
        String finalMessage = "您的" + combinedMessage + "存在异常，请注意";
        Log.d("监测异常报警", "开始播报，文本为：" + finalMessage);
        // 使用 TTS 播报
        TTSUtils.speakText(finalMessage, 1.0f, 1.0f);
        // 清空已报告的报警
        activeAlerts.clear();
        isReporting = false;
    }


    // 手动清除所有未报告的报警
    public synchronized void clearAlerts() {
        activeAlerts.clear();
        isReporting = false;
    }
}

