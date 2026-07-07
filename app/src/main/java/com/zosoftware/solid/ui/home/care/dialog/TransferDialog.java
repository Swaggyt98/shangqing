package com.zosoftware.solid.ui.home.care.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.zosoftware.solid.databinding.DialogTransferBinding;
import com.zosoftware.solid.ui.AppDialog;


public class TransferDialog extends AppDialog<DialogTransferBinding> {
    private String userName;
    private String deviceId;

    public TransferDialog(@NonNull Context context, Activity activity, String userName, String deviceId) {
        super(context, activity);
        this.userName = userName;
        this.deviceId = deviceId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSmallWindow();
    }

    @Override
    public void init() {
        // 设置弹窗标题和内容
        binding.dialogTitle.setText("用户转移通知");
        binding.dialogMessage.setText("用户 " + userName + " 已转移至监护仪 " + deviceId);

        // 设置关闭按钮
        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss(); // 关闭弹窗
            }
        });
    }

    // 设置弹窗大小和位置
    private void setSmallWindow() {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        layoutParams.width = (int) (displayMetrics.widthPixels * 0.8); // 设置弹窗宽度为屏幕宽度的80%
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度自适应
        layoutParams.gravity = Gravity.CENTER; // 弹窗居中显示
        getWindow().setAttributes(layoutParams);
    }
}


