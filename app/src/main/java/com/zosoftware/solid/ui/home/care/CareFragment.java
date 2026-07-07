package com.zosoftware.solid.ui.home.care;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.data.Entry;
import com.zosoftware.solid.MyApplication;
import com.zosoftware.solid.R;
import com.zosoftware.solid.api.Api;
import com.zosoftware.solid.bean.LogItem;
import com.zosoftware.solid.bean.User;
import com.zosoftware.solid.databinding.FragmentCareBinding;
import com.zosoftware.solid.ui.AppFragment;

import com.zosoftware.solid.ui.home.DoneDialog;
import com.zosoftware.solid.ui.home.care.dialog.BPTestDialog;
import com.zosoftware.solid.ui.home.care.dialog.ComprehanDialog;
import com.zosoftware.solid.ui.home.care.dialog.GCSDialog;
import com.zosoftware.solid.ui.home.care.dialog.InjuredAreaDialog;
import com.zosoftware.solid.ui.home.care.dialog.InjuredSerDialog;
import com.zosoftware.solid.ui.home.care.dialog.InjuredTypeDialog;
import com.zosoftware.solid.ui.home.care.dialog.NetworkDialog;
import com.zosoftware.solid.ui.home.care.dialog.PHIDialog;
import com.zosoftware.solid.ui.home.care.dialog.STARTDialog;
import com.zosoftware.solid.ui.home.care.dialog.VoiceDialog;

import com.zosoftware.solid.utils.TensorFlowUtils;
import com.zosoftware.solid.utils.UserManager;
import com.zosoftware.solid.utils.Utils;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import com.zosoftware.solid.utils.ChartHelper;
public class CareFragment extends AppFragment<FragmentCareBinding> implements View.OnClickListener {

    private List<Entry> mData = new ArrayList<>();

    boolean drawing = true;

    // 报警相关
    private final Alert tempAlert = new Alert("体温");
    private final Alert hrAlert = new Alert("心率");
    private final Alert rpmAlert = new Alert("呼吸率");
    private final Alert boAlert = new Alert("血氧");
    private boolean hr_isconnected = false;

    @Override
    public void init() {
        binding.voiceinput.setOnClickListener(this);
        binding.networksetting.setOnClickListener(this);
        binding.injuredarea.setOnClickListener(this);
        binding.injuredtype.setOnClickListener(this);
        binding.injuredser.setOnClickListener(this);
        binding.comprehan.setOnClickListener(this);
        binding.gcs.setOnClickListener(this);
        binding.start.setOnClickListener(this);
        binding.phi.setOnClickListener(this);
        binding.setrecheck.setOnClickListener(this);
        binding.bptest.setOnClickListener(this);
        binding.si.setOnClickListener(this);
        binding.rsig.setOnClickListener(this);

        ChartHelper.initChart(mData,binding.waveView1,25000);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        UserManager.datacallback = ecg -> {
            try {
                if (drawing) {
                    activity.runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            // Set ECG data to the chart
                            //Utils.loginfo("testecg:" + ecg);
                            if (ecg > 30000 || ecg < -20000 || ecg == -64) {
                                binding.sensor1.setVisibility(View.VISIBLE);
                                binding.waveView1.setVisibility(View.GONE);
                                binding.sensor2.setTextColor(Color.YELLOW);
                                hr_isconnected = false;
                            }
                            else{
                                binding.sensor1.setVisibility(View.GONE);
                                binding.waveView1.setVisibility(View.VISIBLE);
                                ChartHelper.addEntry(mData, binding.waveView1, ecg);
                                hr_isconnected = true;
                            }
                            ChartHelper.addEntry(mData, binding.waveView1, ecg);
                            // Get current user
                            User currentUser = UserManager.getCurrentUser();
                            if (currentUser == null) {
                                return; // Handle case where currentUser is null
                            }

                            // Get physiological data from current user
                            String tempStr = currentUser.temp;
                            String hrStr = currentUser.hr;
                            String rpmStr = currentUser.bp;
                            String sbpStr = currentUser.sbpressure;
                            String dbpStr = currentUser.dbpressure;
                            String boStr = currentUser.bo;

                            // Convert to numerical values with default values for empty strings
                            double temp = tempStr.isEmpty() ? 0.0 : Double.parseDouble(tempStr);
                            int hr = hrStr.isEmpty() ? 0 : Integer.parseInt(hrStr);
                            int rpm = rpmStr.isEmpty() ? 0 : Integer.parseInt(rpmStr);
                            int sbp = sbpStr.isEmpty() ? 0 : (int) Float.parseFloat(sbpStr);
                            int dbp = dbpStr.isEmpty() ? 0 : (int) Float.parseFloat(dbpStr);
                            int bo = boStr.isEmpty() ? 0 : (int) Float.parseFloat(boStr);

                            // Set text values
                            binding.temp.setText(temp + "℃");
                            binding.bpm.setText(hr + "次/分钟");
                            binding.rpm.setText(rpm + "次/分钟");
                            binding.npb.setText(sbp + "/" + dbp + " mmHg");
                            binding.bo.setText(bo + "%");

                            // Check for abnormal values and set text color to red
                            int warningColor = Color.RED; // Set red color for warning

                            // Temperature range check
                            if (temp == 99.0){
                                binding.temp.setText("未连接温度传感器");
                                binding.temp.setTextColor(Color.YELLOW);
                            }
                            else{
                                binding.temp.setText(temp + "℃");
                                if (temp < 36.0 || temp > 37.2) {
                                    binding.temp.setTextColor(warningColor);
                                    // Set text color to red if out of range
                                    tempAlert.alertResponse();
                                } else {
                                    binding.temp.setTextColor(Color.WHITE); // Reset to default color
                                    tempAlert.reset();
                                }
                            }

                            // Heart rate range check
                            if (hr == 0){
                                binding.bpm.setText("未连接血氧传感器");
                                binding.bpm.setTextColor(Color.YELLOW);
                            }
                            else{
                                binding.bpm.setText(hr + "次/分钟");
                                if (hr < 50 || hr > 100) {
                                    binding.bpm.setTextColor(warningColor); // Set text color to red if out of range
                                    hrAlert.alertResponse();
                                } else {
                                    binding.bpm.setTextColor(Color.WHITE); // Reset to default color
                                    hrAlert.reset();
                                }
                            }

                            // Respiration rate range check
                            if (!hr_isconnected){
                                binding.rpm.setText("未连接心电传感器");
                                binding.rpm.setTextColor(Color.YELLOW);
                            }
                            else{
                                binding.rpm.setText(rpm + "次/分钟");
                                if (rpm < 12 || rpm > 30) {
                                    binding.rpm.setTextColor(warningColor); // Set text color to red if out of range
                                    rpmAlert.alertResponse();
                                } else {
                                    binding.rpm.setTextColor(Color.WHITE); // Reset to default color
                                    rpmAlert.reset();
                                }
                            }

                            // Blood pressure range check (separate SBP and DBP checks)
                            if (sbp == 0 || dbp == 0){
                                binding.npb.setText("未测量血压");
                                binding.npb.setTextColor(Color.WHITE);
                            }
                            else{
                                binding.npb.setText(sbp + "/" + dbp + " mmHg");
                                if (sbp < 90 || sbp > 139) {
                                    // High pressure abnormality (SBP)
                                    binding.npb.setTextColor(warningColor); // Set red color for abnormal SBP value
                                } else {
                                    // Normal SBP
                                    binding.npb.setTextColor(Color.WHITE); // Reset to default color for SBP
                                }

                                if (dbp < 60 || dbp > 89) {
                                    // Low pressure abnormality (DBP)
                                    binding.npb.setTextColor(warningColor); // Set red color for abnormal DBP value
                                } else {
                                    // Normal DBP
                                    binding.npb.setTextColor(Color.WHITE); // Reset to default color for DBP
                                }
                            }

                            // Blood oxygen level range check
                            if (bo == 0){
                                binding.bo.setText("未连接血氧传感器");
                                binding.bo.setTextColor(Color.YELLOW);
                            }
                            else{
                                binding.bo.setText(bo + "%");
                                if (bo < 95 || bo > 100) {
                                    binding.bo.setTextColor(warningColor); // Set text color to red if out of range
                                    boAlert.alertResponse();
                                } else {
                                    binding.bo.setTextColor(Color.WHITE); // Reset to default color
                                    boAlert.reset();
                                }
                            }
/* Blood oxygen level range check and handle bo == 0 case
                            if (bo == 0) {
                                // Show Toast message if blood oxygen is 0
                                Toast.makeText(activity, "请佩戴血氧传感器", Toast.LENGTH_SHORT).show();
                                binding.bo.setText(""); // Clear the blood oxygen value text
                                binding.bo.setTextColor(Color.WHITE); // Reset text color to white
                            } else {
                                binding.bo.setText(bo + "%"); // Set actual blood oxygen value

                                // Blood oxygen level range check
                                if (bo < 95 || bo > 100) {
                                    binding.bo.setTextColor(warningColor); // Set text color to red if out of range
                                } else {
                                    binding.bo.setTextColor(Color.WHITE); // Reset to default color
                                }
                            }*/

                        }
                    });
                } else {
                    // Handle the non-drawing state
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };






        binding.injuredSeverity1.setOnClickListener(clickListener);
        binding.injuredSeverity2.setOnClickListener(clickListener);
        binding.injuredSeverity3.setOnClickListener(clickListener);
        binding.injuredSeverity4.setOnClickListener(clickListener);



        binding.saveresult.setOnClickListener(view -> {
            if(UserManager.getCurrentUser() != null) {
                if(UserManager.getCurrentUser().disposal_plan.length()<=0 ) {
                    Utils.toastinfo(context,"当前未生成处置方案！");
                    return;
                }
                LogItem logItem = UserManager.getCurrentUser().toLogItem();
                JSONObject jsonObject = (JSONObject) JSON.toJSON(logItem);
                Api.Post(context, Api.addlog, jsonObject, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        try {
                            String result = response.body().string();
                            Utils.loginfo( Api.addlog);
                            Utils.loginfo(result);
                            JSONObject jsonObject1 = JSONObject.parseObject(result);
                            if(jsonObject1.containsKey("error") || jsonObject1.containsKey("error")){

                            }else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DoneDialog dialog = new DoneDialog(context,activity);
                                        dialog.show();
                                        Utils.toastinfo(context,"上传成功！");
                                    }
                                });
                            }
                            response.body().close();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else {
                Utils.toastinfo(context,"未选择救治人员");

            }
        });



    }
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (UserManager.getCurrentUser() == null) {
                Utils.toastinfo(context, "未选择救治人员");
                return;
            }

            //   if (UserManager.getCurrentUser().sbpressure.length() <= 1) {
            //      Utils.toastinfo(context, "缺 SBP");
            //      return;
            //   }
            if (UserManager.getCurrentUser().hr.length() <= 1) {
                Utils.toastinfo(context, "缺 HR");
                return;
            }
            if (UserManager.getCurrentUser().bp.length() <= 1) {
                Utils.toastinfo(context, "缺 rr");
                return;
            }
            if (UserManager.getCurrentUser().bo.length() <= 1) {
                Utils.toastinfo(context, "缺 SpO2");
                return;
            }
            if (UserManager.getCurrentUser().temp.length() <= 1) {
                Utils.toastinfo(context, "缺 temp");
                return;
            }

            float[] inputs = new float[14];
            try {
                inputs[0] = Float.parseFloat(UserManager.getCurrentUser().sbpressure);
                inputs[1] = Float.parseFloat(UserManager.getCurrentUser().hr);
                inputs[2] = Float.parseFloat(UserManager.getCurrentUser().bp);
                inputs[3] = Float.parseFloat(UserManager.getCurrentUser().temp);
                inputs[4] = Float.parseFloat(UserManager.getCurrentUser().bo);
                inputs[5] = 1f;  // Preset value for field 5
                inputs[6] = 1f;  // Preset value for field 6
                inputs[7] = 1f;  // Preset value for field 7
                inputs[8] = 1f;  // Preset value for field 8
                inputs[9] = 1f;  // Preset value for field 9
                inputs[10] = UserManager.getCurrentUser().GCS_score;
                inputs[11] = UserManager.getCurrentUser().PHI_score;
                inputs[12] = UserManager.getCurrentUser().rsig;
                inputs[13] = UserManager.getCurrentUser().si;

                float[] result = TensorFlowUtils.doInference(context, inputs);
                updateSeverity(result);

            } catch (NumberFormatException e) {
                Utils.toastinfo(context, "输入数据格式错误");
            } catch (IOException e) {
                e.printStackTrace();
                Utils.toastinfo(context, "TensorFlow 推理错误");
            }
        }
        /*
                private void updateSeverity(float[] result) {
                    if (result != null && result.length > 0) { // 确保数组不为空且至少有一个元素
                        int severityIndex = (int) Math.round(result[0]); // 假设第一个元素表示伤害的严重程度，将其四舍五入为整数

                        // 显示推理结果
                        Utils.toastinfo(context, "推理结果: " + Arrays.toString(result));

                        // 重置所有严重程度的背景
                        binding.injuredSeverity1.setBackgroundResource(R.color.bgs6);
                        binding.injuredSeverity2.setBackgroundResource(R.color.bgs6);
                        binding.injuredSeverity3.setBackgroundResource(R.color.bgs6);
                        binding.injuredSeverity4.setBackgroundResource(R.color.bgs6);

                        // 根据严重程度设置相应的背景和用户信息
                        String severityLabel = "";

                        switch (severityIndex) {
                            case 0:
                                severityLabel = "轻伤";
                                binding.injuredSeverity1.setBackgroundResource(R.color.serial4);
                                break;
                            case 1:
                                severityLabel = "中度伤";
                                binding.injuredSeverity2.setBackgroundResource(R.color.serial2);
                                break;
                            case 2:
                                severityLabel = "重伤";
                                binding.injuredSeverity3.setBackgroundResource(R.color.red_sl);
                                break;
                            case 3:
                                severityLabel = "危重伤";
                                binding.injuredSeverity4.setBackgroundResource(R.color.gray_red);
                                break;
                            default:
                                // 处理未知或无效的严重程度
                                severityLabel = "未知";
                                break;
                        }

                        // 更新当前用户的伤害严重程度信息
                        if (UserManager.getCurrentUser() != null) {
                            UserManager.getCurrentUser().injured_severity = severityLabel;
                        }
                    }
                }*/
        private void updateSeverity(float[] result) {
            if (result != null && result.length == 4) { // 确保数组有4个元素
                // 找到数组中的最大值及其对应的索引
                int severityIndex = 0;
                float maxValue = result[0];

                for (int i = 1; i < result.length; i++) {
                    if (result[i] > maxValue) {
                        maxValue = result[i];
                        severityIndex = i;
                    }
                }

                // 显示推理结果
                Utils.toastinfo(context, "推理结果: " + Arrays.toString(result));

                // 重置所有严重程度的背景
                binding.injuredSeverity1.setBackgroundResource(R.color.bgs6);
                binding.injuredSeverity2.setBackgroundResource(R.color.bgs6);
                binding.injuredSeverity3.setBackgroundResource(R.color.bgs6);
                binding.injuredSeverity4.setBackgroundResource(R.color.bgs6);

                // 根据严重程度设置相应的背景和用户信息
                String severityLabel = "";

                switch (severityIndex) {
                    case 0:
                        severityLabel = "轻伤";
                        binding.injuredSeverity1.setBackgroundResource(R.color.serial4);
                        break;
                    case 1:
                        severityLabel = "中度伤";
                        binding.injuredSeverity2.setBackgroundResource(R.color.serial2);
                        break;
                    case 2:
                        severityLabel = "重伤";
                        binding.injuredSeverity3.setBackgroundResource(R.color.red_sl);
                        break;
                    case 3:
                        severityLabel = "危重伤";
                        binding.injuredSeverity4.setBackgroundResource(R.color.gray_red);
                        break;
                    default:
                        severityLabel = "未知";
                        break;
                }

                // 更新当前用户的伤害严重程度信息
                if (UserManager.getCurrentUser() != null) {
                    UserManager.getCurrentUser().injured_severity = severityLabel;
                }
            }
        }

    };

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (UserManager.getCurrentUser() != null && drawing && UserManager.getCurrentUser().xindian.size() > 0) {
                // 获取要绘制的值
                float value = UserManager.getCurrentUser().xindian.get(UserManager.getCurrentUser().xindian.size() - 1);

                // 打印日志，记录即将绘制的数值
                Log.d("ECGPlot", "绘制的数值: " + value);

                // 添加数据到图表
                ChartHelper.addEntry(mData, binding.waveView1, value);

                // 移除已绘制的数据点
                UserManager.getCurrentUser().xindian.remove(UserManager.getCurrentUser().xindian.size() - 1);
            }
            // 调整调用间隔为2毫秒
            handler.postDelayed(this, 2);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.setCurrentActivityContext(getActivity(), getActivity());
        showuserinfo();
//        handler.post(runnable);
    }

    private void showuserinfo() {
        if (UserManager.getCurrentUser() == null) {
            return;
        }
        drawing = true;
        binding.username.setText( UserManager.getCurrentUser().username);
        binding.userid.setText( UserManager.getCurrentUser().userid);
        binding.bloodtype.setText( UserManager.getCurrentUser().bloodtype);
        binding.injuredSeverity1.setBackgroundResource(R.color.bgs6);
        binding.injuredSeverity2.setBackgroundResource(R.color.bgs6);
        binding.injuredSeverity3.setBackgroundResource(R.color.bgs6);
        binding.injuredSeverity4.setBackgroundResource(R.color.bgs6);
        if(UserManager.getCurrentUser().injured_severity.equals(binding.injuredSeverity1.getText().toString().trim()))
            binding.injuredSeverity1.setBackgroundResource(R.color.serial4);
        if(UserManager.getCurrentUser().injured_severity.equals(binding.injuredSeverity2.getText().toString().trim()))
            binding.injuredSeverity2.setBackgroundResource(R.color.serial2);
        if(UserManager.getCurrentUser().injured_severity.equals(binding.injuredSeverity3.getText().toString().trim()))
            binding.injuredSeverity3.setBackgroundResource(R.color.red_sl);
        if(UserManager.getCurrentUser().injured_severity.equals(binding.injuredSeverity4.getText().toString().trim()))
            binding.injuredSeverity4.setBackgroundResource(R.color.gray_red);

        StringBuilder s = new StringBuilder();
        for (String res:UserManager.getCurrentUser().injured_area) {
            s.append(res).append(",");
        }
        if(s.length() > 0)
            s = new StringBuilder(s.substring(0, s.length() - 1));
        binding.injuredarea.setText(s.toString());

        s = new StringBuilder();
        for (String res:UserManager.getCurrentUser().injured_ser) {
            s.append(res).append(",");
        }
        if(s.length() > 0)
            s = new StringBuilder(s.substring(0, s.length() - 1));
        binding.injuredser.setText(s.toString());

        s = new StringBuilder();
        for (String res:UserManager.getCurrentUser().injured_type) {
            s.append(res).append(",");
        }
        if(s.length() > 0)
            s = new StringBuilder(s.substring(0, s.length() - 1));
        binding.injuredtype.setText(s.toString());

        s = new StringBuilder();
        for (String res:UserManager.getCurrentUser().comprehan) {
            s.append(res).append(",");
        }
        if(s.length() > 0)
            s = new StringBuilder(s.substring(0, s.length() - 1));
        binding.comprehan.setText(s.toString());

        binding.gcs.setText(UserManager.getCurrentUser().GCS_score+"");
        binding.phi.setText(UserManager.getCurrentUser().PHI_score+"");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        Log.d("TAG", "onDestroy: ");
        drawing = false;
    }

    @Override
    public void initdata() {

    }

    @Override
    public void onClick(View view) {
        if(UserManager.getCurrentUser()==null)
            Utils.toastinfo(context,"未选择救治人员");
        if(view.getId()== R.id.voiceinput) {
            VoiceDialog voiceDialog = new VoiceDialog(context,activity);
            voiceDialog.callBack = result ->   {
                showuserinfo();
            };
            voiceDialog.show();
        }
        if (view.getId() == R.id.si) {
            if (UserManager.getCurrentUser().hr == null || UserManager.getCurrentUser().hr.isEmpty()) {
                Utils.toastinfo(context, "HR 未设置");
                return;
            }
            if (UserManager.getCurrentUser().sbpressure == null || UserManager.getCurrentUser().sbpressure.isEmpty()) {
                Utils.toastinfo(context, "收缩压未设置");
                return;
            }

            float sbp = Float.parseFloat(UserManager.getCurrentUser().sbpressure);
            if (sbp == 0) {
                Utils.toastinfo(context, "收缩压未设置");
                return;
            }
            float hr = Float.parseFloat(UserManager.getCurrentUser().hr);
            UserManager.getCurrentUser().si = hr / sbp;
            binding.si.setText(UserManager.getCurrentUser().si+"");
        }

        if (view.getId() == R.id.rsig) {
            if (UserManager.getCurrentUser().hr == null || UserManager.getCurrentUser().hr.isEmpty()) {
                Utils.toastinfo(context, "HR 未设置");
                return;
            }
            if (UserManager.getCurrentUser().sbpressure == null || UserManager.getCurrentUser().sbpressure.isEmpty()) {
                Utils.toastinfo(context, "收缩压未设置");
                return;
            }
            if (UserManager.getCurrentUser().GCS_score == 0) {
                Utils.toastinfo(context, "GCS 评分未设置");
                return;
            }

            float sbp = Float.parseFloat(UserManager.getCurrentUser().sbpressure);
            float hr = Float.parseFloat(UserManager.getCurrentUser().hr);
            UserManager.getCurrentUser().rsig = hr / sbp * UserManager.getCurrentUser().GCS_score;
            binding.rsig.setText(UserManager.getCurrentUser().rsig+"");
        }

        if(view.getId()== R.id.networksetting) {
            new NetworkDialog(context,activity).show();
        }
        if(view.getId()== R.id.setrecheck) {
            if(UserManager.getCurrentUser() != null){
                UserManager.getCurrentUser().setrecheck = !UserManager.getCurrentUser().setrecheck;
                Utils.toastinfo(context,UserManager.getCurrentUser().setrecheck ? "已设置为二检" : "已取消设置二检");
            }
        }
        if(view.getId()== R.id.injuredarea) {
            InjuredAreaDialog dialog =  new InjuredAreaDialog(context,activity);
            dialog.callBack = result -> showuserinfo();
            dialog.show();
        }
        if(view.getId()== R.id.injuredtype) {
            InjuredTypeDialog dialog =  new InjuredTypeDialog(context,activity);
            dialog.callBack = result -> showuserinfo();
            dialog.show();
        }
        if(view.getId()== R.id.injuredser) {
            InjuredSerDialog dialog =  new InjuredSerDialog(context,activity);
            dialog.callBack = result -> showuserinfo();
            dialog.show();
        }
        if(view.getId()== R.id.comprehan) {
            ComprehanDialog dialog =  new ComprehanDialog(context,activity);
            dialog.callBack = result -> showuserinfo();
            dialog.show();
        }
        if(view.getId()== R.id.bptest) {
            BPTestDialog dialog =  new BPTestDialog(context,activity);
            dialog.callBack = result -> showuserinfo();
            dialog.show();
        }
        if (view.getId() == R.id.gcs) {
            GCSDialog gcsDialog = new GCSDialog(context,activity);
            gcsDialog.callBack = result -> showuserinfo();
            gcsDialog.show();
        }
        if (view.getId() == R.id.start) {
            STARTDialog startDialog = new STARTDialog(context,activity);
            startDialog.callBack = result -> {
                if (UserManager.getCurrentUser() != null) {
                    binding.startState1.setChecked(false);
                    binding.startState2.setChecked(false);
                    binding.startState3.setChecked(false);
                    binding.startState4.setChecked(false);


                    if (UserManager.getCurrentUser().is_able_to_walk && UserManager.getCurrentUser().is_able_to_breath) {
                        binding.startState1.setChecked(true);
                        return;
                    }else if( ! UserManager.getCurrentUser().is_qidao_breath ){
                        binding.startState4.setChecked(true);
                        return;
                    }else if(UserManager.getCurrentUser().is_breath_la_30  ){
                        binding.startState3.setChecked(true);
                        return;
                    }else if( UserManager.getCurrentUser().is_maoxichongying_lg_2 ){
                        binding.startState3.setChecked(true);
                        return;
                    }else if( UserManager.getCurrentUser().mind_clear ){
                        binding.startState2.setChecked(true);
                        return;
                    }


//                    if (UserManager.getCurrentUser().is_able_to_walk && UserManager.getCurrentUser().is_able_to_breath) {
//                        binding.startState1.setChecked(true);
//                        return;
//                    }
//
//                    if (UserManager.getCurrentUser().mind_clear) {
//                        binding.startState2.setChecked(true);
//                    }else
//                        binding.startState2.setChecked(false);
//
//                    if (UserManager.getCurrentUser().is_breath_la_30 ||
//                            UserManager.getCurrentUser().is_qidao_breath ||
//                            UserManager.getCurrentUser().is_maoxichongying_lg_2 ) {
//                        binding.startState3.setChecked(true);
//                    }else
//                        binding.startState3.setChecked(false);
//
//                    if (!UserManager.getCurrentUser().is_qidao_breath) {
//                        binding.startState4.setChecked(true);
//                    }else
//                        binding.startState4.setChecked(false);
                }
            };
            startDialog.show();
        }
        if(view.getId()== R.id.phi) {

            PHIDialog phiDialog = new PHIDialog(context,activity);
            phiDialog.callBack = result -> {
                binding.phi.setText(result.toString());
            };
            phiDialog.show();
        }
    }
}