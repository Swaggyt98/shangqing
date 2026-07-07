package com.zosoftware.solid.ui.home.care;

import android.util.Log;

import com.zosoftware.solid.utils.AlertManager;


public class Alert {

    private static final String TAG = "监测异常报警";
    // 报警相关属性
    private long lastAlertTime = -1;    // 上次报警时间
    private boolean isAbnormal = false;  // 当前是否处于异常状态
    private String type;     // 参数类型

    public Alert(String type) {
        this.type = type;
        Log.d(TAG, "创建" + type + "报警对象");
    }

    public Alert() {
    }

    public void alertResponse() {
        // 报警响应逻辑
        long currentTime = System.currentTimeMillis();
        if (!isAbnormal) {
            // 首次进入异常状态
            lastAlertTime = currentTime;
            isAbnormal = true;
        } else {
            // 检查异常是否超过10秒
            if (currentTime - lastAlertTime >= 10000) {
                AlertManager.getInstance().addAlert(type);
                lastAlertTime = currentTime;
            }
        }
    }

    public void reset() {
        // 重置状态
        isAbnormal = false;
        lastAlertTime = -1;
    }

}
