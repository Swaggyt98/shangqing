package com.zosoftware.solid.ui.home.device;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.zosoftware.solid.MyApplication;
import com.zosoftware.solid.R;
import com.zosoftware.solid.bean.User;
import com.zosoftware.solid.databinding.ActivityDeviceBinding;
import com.zosoftware.solid.ui.AppActivity;
import com.zosoftware.solid.utils.BleUtil;
import com.zosoftware.solid.utils.MqttManager;
import com.zosoftware.solid.utils.PermissionUtils;
import com.zosoftware.solid.utils.UserManager;
import com.zosoftware.solid.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DeviceActivity extends AppActivity<ActivityDeviceBinding> {

    boolean scanning = false;
    String[] permission_list = new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT};
    int code = 19923;
    List<BluetoothDevice> alldeviceslist = new ArrayList<>();
    List<BluetoothDevice> connecteddeviceslist = new ArrayList<>();
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_BT_PERMISSION = 1001;
    private IntentIntegrator qrScan;
    MqttManager mqttManager = MyApplication.getMqttManager();
    private ScheduledExecutorService mExecutor;

    @SuppressLint("MissingPermission")
    BaseQuickAdapter all_devices_adapter = new BaseQuickAdapter<BluetoothDevice, QuickViewHolder>() {
        @NonNull
        @Override
        protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
            return new QuickViewHolder(R.layout.bleitem, viewGroup);
        }
        @Override
        protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable BluetoothDevice objects) {
            ((TextView) quickViewHolder.findView(R.id.devicename)).setText(objects.getName());
            ((TextView) quickViewHolder.findView(R.id.deviceaddress)).setText(objects.getAddress());
            for (User u : UserManager.userList) {
                if (objects.getAddress().trim().equals(u.device.getAddress().trim())) {
                    ((Button) quickViewHolder.findView(R.id.connectbtn)).setText("断开");
                }
            }
        }
    };

    @SuppressLint("MissingPermission")
    BaseQuickAdapter connected_devices_adapter = new BaseQuickAdapter<BluetoothDevice, QuickViewHolder>() {
        @NonNull
        @Override
        protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
            return new QuickViewHolder(R.layout.bleitem, viewGroup);
        }
        @Override
        protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable BluetoothDevice objects) {
            ((TextView) quickViewHolder.findView(R.id.devicename)).setText(objects.getName());
            ((TextView) quickViewHolder.findView(R.id.deviceaddress)).setText(objects.getAddress());
            for (User u : UserManager.userList) {
                if (objects.getAddress().trim().equals(u.device.getAddress().trim())) {
                    ((Button) quickViewHolder.findView(R.id.connectbtn)).setText("断开");
                }
            }
        }

    };

    @Override
    @SuppressLint("MissingPermission")
    public void init() {
        // 初始化设备列表
        binding.notconnectedlist.setLayoutManager(new LinearLayoutManager(activity));
        binding.notconnectedlist.setAdapter(all_devices_adapter);
        binding.connectedlist.setLayoutManager(new LinearLayoutManager(activity));
        binding.connectedlist.setAdapter(connected_devices_adapter);

        // 初始化二维码扫描组件
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false);
        qrScan.setPrompt("将二维码放入框内");
        qrScan.setBeepEnabled(false);
        qrScan.setBarcodeImageEnabled(true);

        // 绑定二维码扫描按钮
        binding.qrScanBtn.setOnClickListener(view -> {
            if (checkCameraPermission()) {
                qrScan.initiateScan();
            } else {
                requestCameraPermission();
            }
        });

        BleUtil.getInstance().setContext(context);
        BleUtil.getInstance().setBTUtilListener(new BleUtil.BTUtilListener() {
            @Override
            public void onLeScanStart() {
                setstate("Scanning ：");
            }

            @Override
            public void onLeScanStop() {
                setstate("Scan stop ：");
            }

            @Override
            public void onLeScanDevices(List<BluetoothDevice> listDevice) {
                setstate("onLeScanDevices");
                for (BluetoothDevice d : listDevice) {
                    adddevicetolist(d);
                }
            }

            @Override
            public void onConnected(BluetoothDevice mCurDevice) {
                setstate("Connected ：" + mCurDevice.getName());
            }

            @Override
            public void onDisConnected(BluetoothDevice mCurDevice) {
                setstate("Disconnected ：");
            }

            @Override
            public void onConnecting(BluetoothDevice mCurDevice) {
                setstate("Connecting ：");
            }

            @Override
            public void onDisConnecting(BluetoothDevice mCurDevice) {

            }

            @Override
            public void onStrength(int strength) {

            }

            @Override
            public void onModel(int model) {

            }
        });

        all_devices_adapter.addOnItemChildClickListener(R.id.connectbtn, (baseQuickAdapter, view, i) -> {
            Utils.loginfo("try connect to dev");
            try {
                boolean isConnected = false;
                BluetoothDevice device = alldeviceslist.get(i);
                Button connectBtn = view.findViewById(R.id.connectbtn);
                for (User user : UserManager.userList) {
                    if (user.bluetoothGatt.getDevice().getAddress().equals(device.getAddress())) {
                        isConnected = true;
                        break;
                    }
                }
                if (!isConnected) {
                    User user = new User();
                    user.device = alldeviceslist.get(i);
                    user.connectGatt(context);
                    UserManager.userList.add(user);
                    addconnecteddevice(user.device); // 添加设备至已配对列表
                    connectBtn.setText("已连接");
                    all_devices_adapter.notifyDataSetChanged();
                    connected_devices_adapter.notifyDataSetChanged();
                    setstate("Connect Success");
                }
                else {
                    // 设备已连接，尝试断开连接
                    for (User user : UserManager.userList) {
                        if (user.bluetoothGatt.getDevice().getAddress().equals(device.getAddress())) {
                            user.disConnGatt();; // 调用断开连接的方法
                            UserManager.userList.remove(user); // 从列表中移除该用户
                            connectBtn.setText("连接");
                            all_devices_adapter.notifyDataSetChanged();
                            connected_devices_adapter.notifyDataSetChanged();
                            setstate("Disconnect Success");
                            break; // 断开连接后退出循环
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                setstate("Connect Fail");
            }
        });

        connected_devices_adapter.addOnItemChildClickListener(R.id.connectbtn, (baseQuickAdapter, view, i) -> {
            Utils.loginfo("try disconnect to dev");
            try {
                boolean isConnected = false;
                BluetoothDevice device = connecteddeviceslist.get(i);
                Button connectBtn = view.findViewById(R.id.connectbtn);
                for (User user : UserManager.userList) {
                    if (user.bluetoothGatt.getDevice().getAddress().equals(device.getAddress())) {
                        isConnected = true;
                        break;
                    }
                }
                if (!isConnected) {
                    User user = new User();
                    user.device = connecteddeviceslist.get(i);
                    user.connectGatt(context);
                    UserManager.userList.add(user);
                    connectBtn.setText("断开");
                    all_devices_adapter.notifyDataSetChanged();
                    connected_devices_adapter.notifyDataSetChanged();
                    setstate("Connect Success");
                }
                else {
                    // 设备已连接，尝试断开连接
                    for (User user : UserManager.userList) {
                        if (user.bluetoothGatt.getDevice().getAddress().equals(device.getAddress())) {
                            user.disConnGatt();; // 调用断开连接的方法
                            UserManager.userList.remove(user); // 从列表中移除该用户
                            connectBtn.setText("连接");
                            all_devices_adapter.notifyDataSetChanged();
                            connected_devices_adapter.notifyDataSetChanged();
                            setstate("Disconnect Success");
                            break; // 断开连接后退出循环
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                setstate("Connect Fail");
            }
        });

        binding.scanbtn.setOnClickListener(view -> {
            BleUtil.getInstance().scanLeDevice(true);
            Utils.toastinfo(context, "开始扫描");
            scanning = true;
        });
    }
    // 新增相机权限检查方法
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    // 新增相机权限请求方法
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }
    private void setstate(String s) {
        Utils.loginfo(s);
    }

    @Override
    public void initdata() {
        getPermissionandDevice();
    }

    public void getPermissionandDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean scanPermission = PermissionUtils.hasPermission(this, Manifest.permission.BLUETOOTH_SCAN);
            boolean advertisePermission = PermissionUtils.hasPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE);
            boolean connectPermission = PermissionUtils.hasPermission(this, Manifest.permission.BLUETOOTH_CONNECT);
            boolean locationPermission = PermissionUtils.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            boolean coarselocationPermission = PermissionUtils.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (!scanPermission || !advertisePermission || !connectPermission || !coarselocationPermission || !locationPermission) {
                PermissionUtils.requestPermission(this, new String[]{
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_ADVERTISE,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                }, code);
            } else {
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 处理 Android 12 之前的版本
            boolean locationPermission = PermissionUtils.hasPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            boolean accessPermission = PermissionUtils.hasPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

            if (!locationPermission || !accessPermission) {
                // 有一个或多个权限未授予，需要申请权限
                PermissionUtils.requestPermission(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                }, code);
            } else {
            }

        } else {
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                qrScan.initiateScan();
            } else {
                Toast.makeText(this, "需要相机权限才能扫描二维码", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == code) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                handleQRResult(result.getContents());
            } else {
                Toast.makeText(this, "扫描取消", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // 新增二维码内容解析
    private void handleQRResult(String content) {
        if (content.startsWith("BT,")) {
            String[] parts = content.split(",");
            if (parts.length >= 3) {
                String deviceName = parts[1];
                String macAddress = parts[2];
                Toast.makeText(this, String.format("识别到蓝牙设备：deviceName:%s",deviceName),Toast.LENGTH_SHORT).show();
                mqttManager.sendBleMessage(content);
                mExecutor = Executors.newSingleThreadScheduledExecutor();
                mExecutor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        // 在这里执行延时后的操作
                        // 注意：如果需要更新 UI，需要使用 runOnUiThread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connectDeviceByQR(deviceName, macAddress);
                            }
                        });
                    }
                }, 2, TimeUnit.SECONDS); // 3 秒后执行
                //connectDeviceByQR(deviceName, macAddress);
            } else {
                Toast.makeText(this, "二维码格式不正确（需包含设备名称和MAC地址）", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "不支持的二维码类型", Toast.LENGTH_SHORT).show();
        }
    }
    // 新增通过二维码连接设备
    private void connectDeviceByQR(String deviceName, String macAddress) {
        // 在调用 connectDeviceByQR 前检查权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                        REQUEST_BT_PERMISSION);
                Toast.makeText(this, "未授权", Toast.LENGTH_SHORT).show();
                return; // 权限未授予时，先不执行后续逻辑
            }
        }
        Log.d("Bluetooth", "Trying to connect to device: " + deviceName + " with MAC: " + macAddress);
        BluetoothDevice device = BleUtil.getInstance().mBtAdapter.getRemoteDevice(macAddress);
        if (device != null) {
            Log.d("Bluetooth", "Device found: " + device.getName());
            boolean isConnected = false;
            for (User user : UserManager.userList) {
                if (user.device.getAddress().equals(macAddress)) {
                    isConnected = true;
                    break;
                }
            }
            if (!isConnected) {
                User user = new User();
                user.device = device;
                user.connectGatt(context);
                UserManager.userList.add(user);
                all_devices_adapter.notifyDataSetChanged();
                setstate("通过二维码连接成功：" + deviceName);
                Toast.makeText(this, "蓝牙连接成功", Toast.LENGTH_SHORT).show();
                addconnecteddevice(user.device);
                connected_devices_adapter.notifyDataSetChanged();
            } else {
                setstate("设备已连接：" + deviceName);
            }
        } else {
            Log.e("Bluetooth", "Device not found with MAC: " + macAddress);
            Toast.makeText(this, "未找到该蓝牙设备", Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("MissingPermission")
    void adddevicetolist(BluetoothDevice device) {

        boolean added = false;
        for (int i = 0; i < alldeviceslist.size(); i++) {
            if (alldeviceslist.get(i).getAddress().equals(device.getAddress())) {
                alldeviceslist.set(i, device);
                added = true;
            }
        }
        if (!added)
            alldeviceslist.add(device);
        all_devices_adapter.submitList(alldeviceslist);
    }
    void addconnecteddevice(BluetoothDevice device) {
        if (!connecteddeviceslist.contains(device)){
            connecteddeviceslist.add(device);
        }
        connected_devices_adapter.submitList(connecteddeviceslist);
    }
}