package com.zosoftware.solid.utils;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.zosoftware.solid.MyApplication;
import com.zosoftware.solid.bean.User;
import com.zosoftware.solid.ui.home.care.dialog.TransferDialog;
//import com.zosoftware.solid.ui.home.care.dialog.TransferDialog;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import info.mqtt.android.service.MqttAndroidClient;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttCallback;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MqttManager {
    protected Context context;
    protected Activity activity;
    private static final String TAG = "MqttManager";
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mqttConnectOptions;
    private ScheduledExecutorService scheduler;
    private int reconnectAttempts = 0;
    private final int MAX_RECONNECT_ATTEMPTS = 5;
    private final int INITIAL_RECONNECT_DELAY = 1000; // 初始重连延迟，单位毫秒

    private String host;
    private String userName;
    private String passWord;
    private String clientId;
    private String mqtt_pub_topic;
    private String mqtt_sub_topic;
    private MqttCallback mqttCallback;
    private static MqttManager instance;

    private MqttManager(String host, String userName, String passWord, String clientId, String mqtt_pub_topic, String mqtt_sub_topic) {
        this.host = host;
        this.userName = userName;
        this.passWord = passWord;
        this.clientId = clientId;
        this.mqtt_pub_topic = mqtt_pub_topic;
        this.mqtt_sub_topic = mqtt_sub_topic;
    }
    public static synchronized MqttManager getInstance(String host, String userName, String passWord, String clientId, String mqtt_pub_topic, String mqtt_sub_topic) {
        if (instance == null) {
            instance = new MqttManager(host, userName, passWord, clientId, mqtt_pub_topic, mqtt_sub_topic);
        }
        return instance;
    }
    public void initMqttClient(Context context) {
        mqttAndroidClient = new MqttAndroidClient(context, host, clientId);
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(userName);
        mqttConnectOptions.setPassword(passWord.toCharArray());
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setConnectionTimeout(10);

        mqttCallback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "MQTT 连接丢失，尝试重连...");
                startReconnect();
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, "收到消息: " + new String(message.getPayload()));
                handleMessage(new String(message.getPayload())); // 新增：处理接收到的消息
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "消息发送完成");
            }
        };
        mqttAndroidClient.setCallback(mqttCallback);
        connectAndSubscribe();
    }
    private void connectAndSubscribe() {
        if (mqttAndroidClient == null) {
            Log.e(TAG, "MQTT 客户端未初始化");
            return;
        }
        if (mqttAndroidClient.isConnected()) {
            Log.d(TAG, "MQTT 已经连接");
            return;
        }
        mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d(TAG, "MQTT 连接成功");
                Utils.loginfo("MQTT 连接成功");
                reconnectAttempts = 0;
                try {
                    // 订阅主题/ble
                    mqttAndroidClient.subscribe(mqtt_sub_topic, 0);
                    Log.d(TAG, "已订阅主题：" + mqtt_sub_topic);
                    Utils.loginfo("已订阅主题：" + mqtt_sub_topic);
                } catch (Exception e) {
                    Log.e(TAG, mqtt_sub_topic + e.getMessage());
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.e(TAG, "MQTT 连接失败: " + exception.getMessage());
                Utils.loginfo("MQTT 连接失败: " + exception.getMessage());
                startReconnect();
            }
        });
    }

    public void disconnect() {
        if (mqttAndroidClient == null || !mqttAndroidClient.isConnected()) {
            return;
        }
        mqttAndroidClient.disconnect();
        Log.d(TAG, "MQTT 已断开连接");
        Utils.loginfo("MQTT 已断开连接");
    }

    private void startReconnect() {
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        int delay = INITIAL_RECONNECT_DELAY * (int) Math.pow(2, reconnectAttempts);
        delay = Math.min(delay, 30000); // 最大重连延迟为30秒

        scheduler.schedule(() -> {
            if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                reconnectAttempts++;
                Log.d(TAG, "尝试重连，第 " + reconnectAttempts + " 次");
                connectAndSubscribe();
            } else {
                Log.e(TAG, "达到最大重连次数，停止重连");
                if (scheduler != null && !scheduler.isShutdown()) {
                    scheduler.shutdown();
                }
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    public boolean isConnected() {
        return mqttAndroidClient != null && mqttAndroidClient.isConnected();
    }

    // 新增：发送消息到主题/ble
    public void sendBleMessage(String content) {
        if (mqttAndroidClient == null || !mqttAndroidClient.isConnected()) {
            Log.e(TAG, "无法发送消息，MQTT客户端未连接");
            Utils.loginfo("无法发送消息，MQTT客户端未连接");
            return;
        }
        try {
            MqttMessage message = new MqttMessage();
            String message2send = content + "," + clientId;
            message.setPayload(message2send.getBytes());
            message.setQos(0);
            message.setRetained(false);
            mqttAndroidClient.publish(mqtt_sub_topic, message);
            Log.d(TAG, mqtt_sub_topic + content);
        } catch (Exception e) {
            Log.e(TAG, mqtt_sub_topic + e.getMessage());
            e.printStackTrace();
        }
    }
    // 新增：处理接收到的消息
    private void handleMessage(String message) {
        // 假设消息格式为 "BT,设备名称,MAC地址"
        if (message.startsWith("BT,")) {
            String[] parts = message.split(",");
            if (parts.length >= 4) {
                String deviceName = parts[1];
                String macAddress = parts[2];
                String senderId = parts[3];
                // 判断是否是自己发送的消息
                if (!senderId.equals(clientId)) {
                    handleBleDeviceMessage(deviceName, macAddress, senderId);
                } else {
                    Log.d(TAG, "忽略自己发送的消息并尝试连接该蓝牙设备");
                }
            } else {
                Log.e(TAG, "消息格式不正确");
            }
        }
    }
    // 新增：处理蓝牙设备消息
    private void handleBleDeviceMessage(String deviceName, String macAddress, String senderId) {
        Log.d(TAG, "处理蓝牙设备消息: " + deviceName + ", " + macAddress);
        // 检查设备是否已经连接
        boolean isConnected = false;
        for (User user : UserManager.userList) {
            if (user.device.getAddress().equals(macAddress)) {
                isConnected = true;
                break;
            }
        }
        if (isConnected) {
            // 如果设备已连接，断开连接
            for (User user : UserManager.userList) {
                if (user.device.getAddress().equals(macAddress)) {
                    String removed_user = user.username;
                    user.disConnGatt();
                    UserManager.userList.remove(user);
                    UserManager.sel_ind = -1;
                    Log.d(TAG, deviceName + "设备已断开");
                    Utils.loginfo(deviceName + "设备已断开");
                    activity = MyApplication.getCurrentActivity();
                    context = MyApplication.getCurrentContext();
                    if (activity != null) {
                        activity.runOnUiThread(() -> {
                            new TransferDialog(
                                    context,
                                    activity,
                                    removed_user, // 用户名
                                    senderId // 监护仪名称
                            ).show();
                        });
                    } else {
                        Log.e(TAG, "activity 为 null");
                    }
                    break;
                }
            }
        }
        else {
            Log.d(TAG,deviceName + "设备未连接" );
        }
    }

}

