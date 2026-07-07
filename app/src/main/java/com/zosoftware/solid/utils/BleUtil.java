package com.zosoftware.solid.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleUtil {

    private static final String TAG = "BleUtil";
    private static final long SCAN_PERIOD = 10000;

    public static String serviceUUID1 = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";//APP发送命令
    public static String readUUID1 = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";//APP 读取命令
    public static String writeUUID1 = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";//APP发送命令

    public static byte[] workModel = {0x02, 0x01};

    private Context mContext;
    private static BleUtil mInstance;

    //作为中央来使用和处理数据；
    private BluetoothGatt mGatt;

    private BTUtilListener mListener;
    public BluetoothAdapter mBtAdapter;
    public List<BluetoothDevice> listDevice;
    private List<BluetoothGatt> bluetoothGatts = new ArrayList<>();

    private BluetoothGattCharacteristic character1;
    private BluetoothGattCharacteristic character2;


    public static synchronized BleUtil getInstance() {
        if (mInstance == null) {
            mInstance = new BleUtil();
        }
        return mInstance;
    }


    public void setContext(Context context) {
        mContext = context;
        init();
    }

    @SuppressLint("MissingPermission")
    public void init() {
        listDevice = new ArrayList<>();
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showToast("BLE不支持此设备!");
            ((Activity) mContext).finish();
        }
        //注：这里通过getSystemService获取BluetoothManager，
        //再通过BluetoothManager获取BluetoothAdapter。BluetoothManager在Android4.3以上支持(API level 18)。
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        ;
        if (mBtAdapter == null || !mBtAdapter.isEnabled()) {
            mBtAdapter.enable();
            /*Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivity(enableBtIntent);*/
        }
    }

    //扫描设备的回调
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!listDevice.contains(device)) {
                        //不重复添加
                        listDevice.add(device);
                        mListener.onLeScanDevices(listDevice);
                        Log.e(TAG, "device:" + device.toString());
                    }
                }
            });
        }
    };

    //扫描设备
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                    Log.e(TAG, "run: stop");
                }
            }, SCAN_PERIOD);
            startScan();
            Log.e(TAG, "start");
        } else {
            stopScan();
            Log.e(TAG, "stop");
        }
    }

    //开始扫描BLE设备
    @SuppressLint("MissingPermission")
    private void startScan() {
        mBtAdapter.startLeScan(mLeScanCallback);
        mListener.onLeScanStart();
    }

    //停止扫描BLE设备
    @SuppressLint("MissingPermission")
    private void stopScan() {
        mBtAdapter.stopLeScan(mLeScanCallback);
        mListener.onLeScanStop();
    }



    //发送进入工作模式请求
    @SuppressLint("MissingPermission")
    public void sendWorkModel() {
        if (character1 != null) {
            character1.setValue(workModel);
            mGatt.writeCharacteristic(character1);
        } else {
            Utils.loginfo("character1 = null");
            Utils.loginfo("character1 " + character1);
            Utils.loginfo("character1 " + character2);
        }
    }

    //发送强度
    @SuppressLint("MissingPermission")
    public void sendStrength(int strength) {
        byte[] strengthModel = {0x01, (byte) strength};
        if (character1 != null) {
            character1.setValue(strengthModel);
            mGatt.writeCharacteristic(character1);
        }
    }


    private void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void setBTUtilListener(BTUtilListener listener) {
        mListener = listener;
    }

    public interface BTUtilListener {
        void onLeScanStart(); // 扫描开始

        void onLeScanStop();  // 扫描停止

        void onLeScanDevices(List<BluetoothDevice> listDevice); //扫描得到的设备

        void onConnected(BluetoothDevice mCurDevice); //设备的连接

        void onDisConnected(BluetoothDevice mCurDevice); //设备断开连接

        void onConnecting(BluetoothDevice mCurDevice); //设备连接中

        void onDisConnecting(BluetoothDevice mCurDevice); //设备连接失败

        void onStrength(int strength); //给设备设置强度

        void onModel(int model); //设备模式
    }
}