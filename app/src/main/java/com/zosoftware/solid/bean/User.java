package com.zosoftware.solid.bean;

import static com.zosoftware.solid.utils.BleUtil.readUUID1;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.bumptech.glide.util.Util;
import com.zosoftware.solid.utils.BleUtil;
import com.zosoftware.solid.utils.SocketUtils;
import com.zosoftware.solid.utils.UserManager;
import com.zosoftware.solid.utils.Utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.UUID;

public class User {
    private static final String TAG = "user ";
    public Thread workthread;
    public BluetoothSocket socket;
    public BluetoothGatt bluetoothGatt;
    public BluetoothDevice device;
    public String username = "";
    public String userid;
    public String bloodtype;
    public String gender;
    public int battary_val_state; //  4	4.2V: 100% ,3 4.0V: 75% ,2 3.7V: 50% ,1 3.4V: 25% ,0 3.0V: 0%
    public String duties;
    public String department;
    public String rank;
    public boolean setrecheck = false;
    public boolean stop_readding = false;
    public boolean stop = false;
    public boolean is_war_wound = false;// 是否为战伤
    public int current_disposal_method = 1; // 当前处置方式
    public int GCS_score = 15;
    public int PHI_score = 8;
    public String anti_infection = "";
    public String anti_shock = "";
    public String evacuation_plan = "";
    public String disposal_plan = "";
    public String emergency_surgery = "";
    public String expert_treatment_recommendations = "";
    public String temp = "";
    public String sbpressure = ""; // 收缩压
    public String dbpressure = ""; // 舒张压

    public String hr = ""; // 心率
    public String npb = ""; // npb
    public String bo = ""; // 血氧
 //   public String bp;

    public String bp = ""; //呼吸率
//    public User() {
 //       this.bp = ""; // 设置呼吸率默认值为 19
 //   }

    public Float bp_float = 0f; //呼吸率

    public String tag_color = "绿色"; //伤员标记的颜色
    public String age = ""; // 血氧
    public float si = 0f; // Shock Index;
    public float rsig = 0f; // Shock Index;
    int maxxindiansize = 50000;
    public float ecg = 0;
    public String ecgnum = "";
    public String injured_area_str = "";
    public String injured_ser_str = "";
    public String injured_type_str = "";
    public String comprehan_str = "";
    public List<Float> xindian = new ArrayList<>();
    public List<Double> bp_arr = new ArrayList<>();

    public List<String> injured_area = new ArrayList<>();
    public List<String> injured_ser = new ArrayList<>();
    public List<String> injured_type = new ArrayList<>();
    public List<String> comprehan = new ArrayList<>();
    public int zhengyan_ability = 0;
    public int language_ability = 0;
    public int sport_ability = 0;
    public boolean is_xiongbu_guanchuan = true;
    public boolean is_able_to_walk = true;
    public boolean is_able_to_breath = true;
    public boolean is_breath_la_30 = true;
    public boolean is_qidao_breath = true;
    public boolean is_maoxichongying_lg_2 = true;
    public int mind_state = 0;
    public boolean mind_clear = true;
    public String injured_severity = "轻伤"; // 0 轻伤，1 中伤，2重伤，3危重
    public int sendstart_BP_test = 0; // 0 idel , 1 start test ,2 testing , 3 test success , 4 test fail
    public boolean reached_peak = false;
    public float[] getxindianFloatarray() {
        float[] Arr = new float[xindian.size()];
        for (int i = 0; i < xindian.size(); i++) {
            Arr[i] = xindian.get(i);
        }
        return Arr;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", userid='" + userid + '\'' +
                ", bloodtype='" + bloodtype + '\'' +
                ", gender='" + gender + '\'' +
                ", department='" + department + '\'' +
                ", rank='" + rank + '\'' +
                ", stop_readding=" + stop_readding +
                ", stop=" + stop +
                '}';
    }

    @SuppressLint("MissingPermission")
    public void disConnGatt() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
    }

    public String command = "";
    //返回中央的状态和周边提供的数据
    @SuppressLint("MissingPermission")
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.e(TAG, "onConnectionStateChange");
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.e(TAG, "STATE_CONNECTED");
                    gatt.discoverServices(); //搜索连接设备所支持的service
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    disConnGatt();
                    Log.e(TAG, "STATE_DISCONNECTED");
                    break;
                case BluetoothProfile.STATE_CONNECTING:
                    Log.e(TAG, "STATE_CONNECTING");
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    Log.e(TAG, "STATE_DISCONNECTING");
                    break;
            }
            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //  开始监听特征的变化
                List<BluetoothGattService> serviceList = gatt.getServices();
                for (int i = 0; i < serviceList.size(); i++) {
                    BluetoothGattService theService = serviceList.get(i);
                    Log.e(TAG, "ServiceName:" + theService.getUuid());
                    List<BluetoothGattCharacteristic> characterList = theService.getCharacteristics();
                    for (int j = 0; j < characterList.size(); j++) {
                        String uuid = characterList.get(j).getUuid().toString();
                        Log.e(TAG, "---CharacterName:" + uuid);
                        if (uuid.equals(readUUID1)) {
                            BluetoothGattCharacteristic character1 = characterList.get(j);
                            boolean resp = gatt.setCharacteristicNotification(character1, true);

                            Log.e(TAG, "---setCharacteristicNotification :" + resp);
                            for (BluetoothGattDescriptor dp : character1.getDescriptors()) {
                                if (dp != null) {
                                    if ((character1.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                                        dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    } else if ((character1.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                                        dp.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                                    }
                                    gatt.writeDescriptor(dp);
                                }
                            }
                        }
                    }
                }
            }
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            byte[] data = characteristic.getValue();
            Utils.loginfo("onCharacteristicRead " + data + "");
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e(TAG, "onCharacteristicWrite  : status " + status);
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            byte[] data = characteristic.getValue();
            String recv = null;
            try {
                recv = new String(data, "UTF-8");
                for (int i = 0; i < recv.length(); i++) {
                    char s = recv.charAt(i);
                    if (',' == s) {

                        dealWithData(command);
                        command = "";
                        continue;
                    }
                    command += s;
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

//            receiveData(characteristic);
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };


    @SuppressLint("MissingPermission")
    public void connectGatt(Context context) {
        bluetoothGatt = device.connectGatt(context, true, mGattCallback);

    }

    @SuppressLint("MissingPermission")
    public void sendCommandToDevice(String command) {
        Log.d (TAG,   "sendCommandToDevice: " + command);
        Utils.loginfo(command);
        if (bluetoothGatt != null) {
            BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(BleUtil.serviceUUID1));
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(BleUtil.writeUUID1));
                if (characteristic != null) {
                    // 设置写入类型
                    characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                    characteristic.setValue(command.getBytes(StandardCharsets.UTF_8));
                    bluetoothGatt.writeCharacteristic(characteristic);
                }
            }
        }
    }

    public void dealWithData(String command) {

        //v:电池电压，单位V
        //t:温度，单位°C
        //b:压力传感器测得的压力值，单位mmgh
        //e:心电波形信号
        //r:呼吸波形信号
        //s:血氧，单位%
        //h:心率，单位次/分钟

        try {

            command = command.trim();
            String[] val = command.split(":");

            if (val.length != 2) {
                Utils.loginfo("error command : " + command);
                return;
            }
            if (val[1].matches(".*[a-zA-Z]+.*")) {
                return;
            }
            if (command.contains("r")) {
                String str = command.split(":")[1];
                bp = str.trim();
            }
            if (command.contains("t")) {
                String str = command.split(":")[1];
                temp = str.trim();
            }

            if (command.contains("s")) {
                String str = command.split(":")[1];
                bo = str.trim();
            }
            if (command.contains("h")) {
                String str = command.split(":")[1];
                hr = str.trim();
            }
            if (command.contains("b")) {
                if (sendstart_BP_test == 2) {
                    String str = command.split(":")[1].trim();
                    Double bp_data_item = Double.parseDouble(str);
                    if (bp_data_item != 0 && bp_data_item < 185) {
                        Utils.loginfo(String.valueOf(bp_data_item));
                        bp_arr.add(bp_data_item);
                        if (bp_data_item >= 179)
                            sendstart_BP_test = 3;
                    }
                }
            }
            if (command.contains("v")) {
                String str = command.split(":")[1];
                float vol = Float.parseFloat(str);
                if (vol >= 3.0) {
                    battary_val_state = 0;
                }
                if (vol >= 3.4) {
                    battary_val_state = 1;
                }
                if (vol >= 3.7) {
                    battary_val_state = 2;
                }
                if (vol >= 4.0) {
                    battary_val_state = 3;
                }
                if (vol >= 4.2) {
                    battary_val_state = 4;
                }
            }
            if (command.contains("e")) {
                String x = command.split(":")[1];
                float x_f = Float.parseFloat(x);
                if (xindian.size() >= maxxindiansize) {
                    xindian.remove(xindian.size() - 1);
                }
                ecg = x_f;
                xindian.add(x_f);
                ecgnum = x_f + "";
                UserManager.datacallback.dealdata(ecg);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void closeThread() {
        try {
            stop = true;
            if (workthread != null)
                workthread.interrupt();

            if (socket != null)
                socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LogItem toLogItem() {
        String str = "";
        for (String s : comprehan) {
            str += s + ",";
        }
  //      if (str.length() <= 0) str.substring(0, str.length() - 2);
        comprehan_str = str;

        str = "";
        for (String s : injured_area) {
            str += s + ",";
        }
     //   if (str.length() <= 0) str.substring(0, str.length() - 2);
        injured_area_str = str;

        str = "";
        for (String s : injured_ser) {
            str += s + ",";
        }
    //    if (str.length() <= 0) str.substring(0, str.length() - 2);
        injured_ser_str = str;

        str = "";
        for (String s : injured_type) {
            str += s + ",";
        }
  //      if (str.length() <= 0) str.substring(0, str.length() - 2);
        injured_type_str = str;


        LogItem logItem = new LogItem();
        logItem.username = username;
        logItem.userid = userid;
        logItem.setrecheck = setrecheck;
        logItem.bloodtype = bloodtype;
        logItem.gender = gender;
        logItem.duties = duties;
        logItem.department = department;
        logItem.srank = rank;
        logItem.is_war_wound = is_war_wound;
        logItem.current_disposal_method = current_disposal_method;
        logItem.GCS_score = GCS_score;
        logItem.PHI_score = PHI_score;
        logItem.anti_infection = anti_infection;
        logItem.anti_shock = anti_shock;
        logItem.evacuation_plan = evacuation_plan;
        logItem.disposal_plan = disposal_plan;
        logItem.emergency_surgery = emergency_surgery;
        logItem.expert_treatment_recommendations = expert_treatment_recommendations;
        logItem.temp = temp;
        logItem.sbpressure = sbpressure;
        logItem.dbpressure = dbpressure;
        logItem.hr = hr;
        logItem.npb = npb;
        logItem.bo = bo;
        logItem.bp = bp;
        logItem.bp_float = bp_float;
        logItem.tag_color = tag_color;
        logItem.age = age;
        logItem.ecg = ecg;
        logItem.ecgnum = ecgnum;
        logItem.injured_area_str = injured_area_str;
        logItem.injured_ser_str = injured_ser_str;
        logItem.injured_type_str = injured_type_str;
        logItem.comprehan_str = comprehan_str;
        logItem.zhengyan_ability = zhengyan_ability;
        logItem.language_ability = language_ability;
        logItem.sport_ability = sport_ability;
        logItem.is_xiongbu_guanchuan = is_xiongbu_guanchuan;
        logItem.is_able_to_walk = is_able_to_walk;
        logItem.is_able_to_breath = is_able_to_breath;
        logItem.is_breath_la_30 = is_breath_la_30;
        logItem.is_qidao_breath = is_qidao_breath;
        logItem.is_maoxichongying_lg_2 = is_maoxichongying_lg_2;
        logItem.mind_state = mind_state;
        logItem.mind_clear = mind_clear;
        logItem.injured_severity = injured_severity;


        return logItem;
    }
}
