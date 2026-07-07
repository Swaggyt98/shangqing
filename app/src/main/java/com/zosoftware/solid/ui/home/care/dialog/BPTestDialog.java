package com.zosoftware.solid.ui.home.care.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.zosoftware.solid.R;
import com.zosoftware.solid.databinding.BpTestDialogBinding;
import com.zosoftware.solid.ui.AppDialog;
import com.zosoftware.solid.utils.BloodPressureAnalyzer;
import com.zosoftware.solid.utils.UserManager;
import com.zosoftware.solid.utils.Utils;

import java.util.ArrayList;

public class BPTestDialog extends AppDialog<BpTestDialogBinding> {

    public BPTestDialog(@NonNull Context context, Activity activity) {
        super(context, activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setsize();
    }

    @Override
    public void init() {
        binding.progress.setVisibility(View.GONE);
        binding.cancel.setOnClickListener(view -> {
            dismiss();
        });

        // 初始化 Spinner 选项
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.bp_mode_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.bpModeSpinner.setAdapter(adapter);

        // 开始测量按钮点击事件
        binding.starttest.setOnClickListener(view -> {
            // 获取选择的测试模式
            final String selectedMode = binding.bpModeSpinner.getSelectedItem().toString();  // 使用 final 关键字
            startBloodPressureTest(selectedMode);
        });
    }


    private void startBloodPressureTest(String mode) {
        // 根据选择的模式处理测试逻辑
        int duration = 0;

        // 选择测量时间
        switch (mode) {
            case "5分钟":
                duration = 5 * 60 * 1000;  // 5分钟转换为毫秒
                break;
            case "15分钟":
                duration = 15 * 60 * 1000; // 15分钟转换为毫秒
                break;
            case "30分钟":
                duration = 30 * 60 * 1000; // 30分钟转换为毫秒
                break;
            default:
                Utils.toastinfo(context, "请选择有效的测试模式");
                return;
        }

        // 开始血压测量
        if (UserManager.getCurrentUser() != null) {
            UserManager.getCurrentUser().bp_arr = new ArrayList<>();
            UserManager.getCurrentUser().sendCommandToDevice("STARTBP");
            UserManager.getCurrentUser().sendstart_BP_test = 2;
            binding.progress.setVisibility(View.VISIBLE);
            binding.form.setVisibility(View.GONE);
        }

        // 模拟测量，经过指定时间后更新结果
        int finalDuration = duration;
        new Thread(() -> {
            int waittime = 0;
            while (true) {
                try {
                    if (UserManager.getCurrentUser() != null && UserManager.getCurrentUser().sendstart_BP_test == 3) {
                        UserManager.getCurrentUser().sendCommandToDevice("STOPBP1");
                        UserManager.getCurrentUser().sendstart_BP_test = 4;
                        dealresult();
                        return;
                    }
                    Thread.sleep(1000);
                    waittime += 1;

                    // 超过指定时间后，退出测量并显示失败
                    if (waittime * 1000 >= finalDuration) {
                        activity.runOnUiThread(() -> {
                            Utils.toastinfo(context, "测试超时失败！");
                            binding.progress.setVisibility(View.GONE);
                            binding.form.setVisibility(View.VISIBLE);
                            binding.result.setText("测试失败！");
                            if (UserManager.getCurrentUser() != null) {
                                UserManager.getCurrentUser().sendCommandToDevice("STOPBP1");
                                UserManager.getCurrentUser().sendstart_BP_test = 4;
                            }
                        });
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setsize() {
        getWindow().setWindowAnimations(R.style.BottomDialogAnimation);
        getWindow().setGravity(Gravity.CENTER);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels / 2;  // 屏幕宽度（像素）
        int height = dm.heightPixels / 2 + dm.heightPixels / 5; // 屏幕高度（像素）
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = width;
        layoutParams.height = height;
        getWindow().setAttributes(layoutParams);
    }

    private void dealresult() {
        if (UserManager.getCurrentUser() != null) {
            try {
                StringBuilder res = new StringBuilder();
                Utils.loginfo(" bp test result array lenght is " + UserManager.getCurrentUser().bp_arr.size());

                double[] sbp_dbp = BloodPressureAnalyzer.main(UserManager.getCurrentUser().bp_arr);
                if (sbp_dbp != null) {
                    activity.runOnUiThread(() -> {
                        Utils.toastinfo(context, "测试完成");
                        binding.progress.setVisibility(View.GONE);
                        binding.form.setVisibility(View.VISIBLE);
                        binding.result.setText(sbp_dbp[0] + "/" + sbp_dbp[1]);
                        UserManager.getCurrentUser().sbpressure = String.format("%.2f", sbp_dbp[0]);
                        UserManager.getCurrentUser().dbpressure = String.format("%.2f", sbp_dbp[1]);
                    });
                } else {
                    Utils.toastinfo(context, "计算失败！");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utils.toastinfo(context, "计算失败！");
                binding.progress.setVisibility(View.GONE);
                binding.form.setVisibility(View.VISIBLE);
            }
        }
    }
}
