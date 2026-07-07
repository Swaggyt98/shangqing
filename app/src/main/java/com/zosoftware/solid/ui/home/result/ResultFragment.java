package com.zosoftware.solid.ui.home.result;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.util.Util;
import com.zosoftware.solid.R;
import com.zosoftware.solid.api.Api;
import com.zosoftware.solid.bean.LogItem;
import com.zosoftware.solid.bean.User;
import com.zosoftware.solid.databinding.FragmentResultBinding;
import com.zosoftware.solid.ui.AppDialog;
import com.zosoftware.solid.ui.AppFragment;
import com.zosoftware.solid.ui.LoginActivity;
import com.zosoftware.solid.ui.MAIN.WaveUtil;
import com.zosoftware.solid.ui.home.DoneDialog;
import com.zosoftware.solid.ui.home.HomeActivity;
import com.zosoftware.solid.ui.home.result.dialog.TreatmentDialog;
import com.zosoftware.solid.utils.UserManager;
import com.zosoftware.solid.utils.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ResultFragment extends AppFragment<FragmentResultBinding> {

    @Override
    public void init() {
        if(UserManager.getCurrentUser() != null) {
            showuserinfo();
        }

    }
    private void showuserinfo() {
        if (UserManager.getCurrentUser() == null) {
            Utils.toastinfo(context,"未选择救治人员");
            return;
        }

        binding.antiInfection.setText(UserManager.getCurrentUser().anti_infection);
        binding.antiShock.setText(UserManager.getCurrentUser().anti_shock);
        binding.emergencySurgery.setText(UserManager.getCurrentUser().emergency_surgery);
        binding.evacuation.setText(UserManager.getCurrentUser().evacuation_plan);

        binding.wonderInjury.setChecked(UserManager.getCurrentUser().is_war_wound);
        binding.noWonderInjury.setChecked(!UserManager.getCurrentUser().is_war_wound);

        binding.username.setText(UserManager.getCurrentUser().username);
        binding.userid.setText(UserManager.getCurrentUser().userid);
        binding.bloodtype.setText(UserManager.getCurrentUser().bloodtype);

        if(UserManager.getCurrentUser().current_disposal_method == 1)
            binding.selfcare.setChecked(true);
        if(UserManager.getCurrentUser().current_disposal_method == 2)
            binding.togecare.setChecked(true);
        if(UserManager.getCurrentUser().current_disposal_method == 3)
            binding.healthcare.setChecked(true);
        if(UserManager.getCurrentUser().current_disposal_method == 4)
            binding.uncare.setChecked(true);
        binding.setrecheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()== R.id.setrecheck) {
                    if(UserManager.getCurrentUser() != null){
                        UserManager.getCurrentUser().setrecheck = !UserManager.getCurrentUser().setrecheck;
                        Utils.toastinfo(context,UserManager.getCurrentUser().setrecheck ? "已设置为二检" : "已取消设置二检");
                    }
                }else
                    Utils.toastinfo(context,"未选择救治人员");
            }
        });
        binding.selfcare.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b && UserManager.getCurrentUser() != null)
                UserManager.getCurrentUser().current_disposal_method = 1;
        });
        binding.togecare.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b && UserManager.getCurrentUser() != null)
                UserManager.getCurrentUser().current_disposal_method = 2;
        });
        binding.healthcare.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b && UserManager.getCurrentUser() != null)
                UserManager.getCurrentUser().current_disposal_method = 3;
        });
        binding.uncare.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b && UserManager.getCurrentUser() != null)
                UserManager.getCurrentUser().current_disposal_method = 4;
        });

        binding.saveresult.setOnClickListener(view -> {
            if(UserManager.getCurrentUser() != null) {
                if(UserManager.getCurrentUser().disposal_plan.length()<=0 ) {
                    Utils.toastinfo(context,"当前未生成处置方案！");
                    return;
                }
                LogItem logItem = UserManager.getCurrentUser().toLogItem();
                JSONObject jsonObject = (JSONObject) JSON.toJSON(logItem);
                Utils.loginfo(jsonObject.toJSONString());
                Api.Post(context, Api.addlog, jsonObject, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        activity.runOnUiThread(() -> Toast.makeText(  context,"提交失败！",Toast.LENGTH_SHORT).show());
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

        binding.generatedeal.setOnClickListener(view -> {
            if(UserManager.getCurrentUser() == null) {
                Utils.toastinfo(context,"未选择救治人员");
                return;
            }
            String injured_area = "";
            for (String s:UserManager.getCurrentUser().injured_area) {
                injured_area += s  +",";
            }
            String injured_type = "";
            for (String s:UserManager.getCurrentUser().injured_type) {
                injured_type += s  +",";
            }
            String injured_ser = "";
            for (String s:UserManager.getCurrentUser().injured_ser) {
                injured_ser += s  +",";
            }
            String results = "伤情评估：伤势："  + UserManager.getCurrentUser().injured_severity
                    +"，伤部为：" + injured_area
                    +"，伤型为：" + injured_ser
                    +"，伤类为：" + injured_type
                    +"，GCS评分：" + UserManager.getCurrentUser().GCS_score + "分"
                    +"，PHI评分：" + UserManager.getCurrentUser().PHI_score + "分"
                    +"，伤员标记为：" + UserManager.getCurrentUser().tag_color
                    +"处置方案：" + UserManager.getCurrentUser().anti_infection
                    +UserManager.getCurrentUser().anti_shock
                    +UserManager.getCurrentUser().emergency_surgery
                    +UserManager.getCurrentUser().evacuation_plan;
            UserManager.getCurrentUser().disposal_plan = results;
            binding.disposePlan.setText(UserManager.getCurrentUser().disposal_plan);

        });
        binding.autofill.setOnClickListener(view -> {
            if(UserManager.getCurrentUser() == null){
                Utils.toastinfo(context,"未选择救治人员");
                return;
            }
            String injured_area = "";
            for (String s:UserManager.getCurrentUser().injured_area) {
                injured_area += s  +",";
            }
            String injured_type = "";
            for (String s:UserManager.getCurrentUser().injured_type) {
                injured_type += s  +",";
            }
            String injured_ser = "";
            for (String s:UserManager.getCurrentUser().injured_ser) {
                injured_ser += s  +",";
            }
            String question = "请针对抗感染、抗休克、紧急手术建议、后送方案、四个方面对受伤人员处置提出提出详细建议和简要操作步骤（每点不超过30个字）。抗感染、抗休克给出具体的用药名称、具体剂量和步骤，紧急手术建议给出具体的详细的操作流程方法，后送方案给出具体的后送方式及送往地人员。直接给出每一点的结果。受伤信息为：伤势为" + UserManager.getCurrentUser().injured_severity
                    +"，伤部为：" + injured_area
                    +"，伤型为：" + injured_ser
                    +"，伤类为：" + injured_type
                    +"，GCS评分：" + UserManager.getCurrentUser().GCS_score + "分"
                    +"，PHI评分：" + UserManager.getCurrentUser().PHI_score + "分";
            Utils.loginfo(question);
            binding.loadding.setVisibility(View.VISIBLE);
            new Thread(() -> {
                String res = Api.callchatgpt(question);
                Utils.loginfo(res);
                activity.runOnUiThread(() -> {
                    binding.loadding.setVisibility(View.GONE);
                    String infection = "";
                    String shock = "";
                    String emegency_surgey = "";
                    String evacuation_plan = "";
                    String[] strings = res.split("\n");
                    for (String s: strings) {
                        if(s.contains("抗感染"))
                            infection  = s.trim();
                        if(s.contains("抗休克"))
                            shock  =  s.trim();
                        if(s.contains("紧急手术"))
                            emegency_surgey =  s.trim();
                         //   emegency_surgey =  s.trim().substring(3);
                        if(s.contains("后送方案"))
                            evacuation_plan =  s.trim();
                    }
                    if(UserManager.getCurrentUser() != null) {
                        UserManager.getCurrentUser().anti_infection = infection;
                        UserManager.getCurrentUser().anti_shock = shock;
                        UserManager.getCurrentUser().emergency_surgery = emegency_surgey;
                        UserManager.getCurrentUser().evacuation_plan = evacuation_plan;

                        binding.antiInfection.setText(UserManager.getCurrentUser().anti_infection);
                        binding.antiShock.setText(UserManager.getCurrentUser().anti_shock);
                        binding.emergencySurgery.setText(UserManager.getCurrentUser().emergency_surgery);
                        binding.evacuation.setText(UserManager.getCurrentUser().evacuation_plan);

                        TreatmentDialog  treatmentDialog = new TreatmentDialog(context,activity);
                        treatmentDialog.callBack = result -> {
                            binding.antiInfection.setText(UserManager.getCurrentUser().anti_infection);
                            binding.antiShock.setText(UserManager.getCurrentUser().anti_shock);
                            binding.emergencySurgery.setText(UserManager.getCurrentUser().emergency_surgery);
                            binding.evacuation.setText(UserManager.getCurrentUser().evacuation_plan);
                        };
                        treatmentDialog.show();
                    }
                });
            }).start();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(UserManager.getCurrentUser() != null) {
            showuserinfo();
        }
    }

    @Override
    public void initdata() {

    }
}