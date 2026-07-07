package com.zosoftware.solid.ui.home.care.dialog;
//这个 NetworkDialog 类是一个自定义对话框，用于配置网络设置和蓝牙 UUID。NetworkDialog 继承自 AppDialog 类，并使用 DialogNetworksettingBinding 进行布局绑定。
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.zosoftware.solid.R;
import com.zosoftware.solid.api.Api;
import com.zosoftware.solid.databinding.DialogNetworksettingBinding;
import com.zosoftware.solid.databinding.VoiceDialogBinding;
import com.zosoftware.solid.ui.AppDialog;

public class NetworkDialog extends AppDialog<DialogNetworksettingBinding> {

    public NetworkDialog(@NonNull Context context, Activity activity) {
        super(context, activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setsmallwindow();
    }

    @Override
    public void init() {
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        binding.bleuuid.setText(context.getSharedPreferences("app", MODE_PRIVATE).getString("bleuuid",Api.bleuuid));
        binding.netaddress.setText(context.getSharedPreferences("app", MODE_PRIVATE).getString("host", Api.ipaddr));
        binding.netport.setText(context.getSharedPreferences("app", MODE_PRIVATE).getString("port", Api.port));
        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("app", MODE_PRIVATE).edit();
                editor.putString("host",binding.netaddress.getText().toString().trim());
                editor.putString("port",binding.netport.getText().toString().trim());
                editor.putString("bleuuid",binding.bleuuid.getText().toString().trim());
                editor.apply();
                editor.commit();

                Api.ipaddr = context.getSharedPreferences("app",MODE_PRIVATE).getString("host","192.168.1.13");
                Api.port = context.getSharedPreferences("app",MODE_PRIVATE).getString("port","8090");
                Api.bleuuid = context.getSharedPreferences("app",MODE_PRIVATE).getString("bleuuid","00001101-0000-1000-8000-00805F9B34FB");
                Api.seturl();
                dismiss();
            }
        });

    }
}
