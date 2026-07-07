package com.zosoftware.solid.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.zosoftware.solid.databinding.ActivityLoginBinding;
import com.zosoftware.solid.api.Api;
import com.zosoftware.solid.ui.MAIN.MainActivity;
import com.zosoftware.solid.ui.home.HomeActivity;
import com.zosoftware.solid.ui.home.care.dialog.NetworkDialog;
import com.zosoftware.solid.utils.RSAUtils;
import com.zosoftware.solid.utils.Utils;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class LoginActivity extends AppActivity<ActivityLoginBinding> {

    private void dologin() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", binding.accounted.getText().toString().trim());
        jsonObject.put("password", RSAUtils.encryptWithPublicKey(binding.password.getText().toString().trim()));

        Api.Post(this, Api.loginurl, jsonObject, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> Toast.makeText(  context,"登录失败！",Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String result = response.body().string();
                    Utils.loginfo( Api.loginurl);
                    Utils.loginfo(result);
                    JSONObject jsonObject1 = JSONObject.parseObject(result);
                    if(jsonObject1.containsKey("error") || jsonObject1.containsKey("error")){
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(  LoginActivity.this,"登录失败！",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(  LoginActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();
                                String token = jsonObject1.getJSONObject("result").getString("token");
                                SharedPreferences.Editor editor = LoginActivity.this.getSharedPreferences("app", MODE_PRIVATE).edit();
                                editor.putString("token", token);
                                editor.putString("user_id", jsonObject1.getJSONObject("result").getString("user_id"));
                                editor.apply();
                                editor.commit();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                            }
                        });
                    }
                    response.body().close();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void init() {


        binding.loginbtn.setOnClickListener(view -> {
            if(
                binding.accounted.getText().toString().length()<=0 ||
                binding.password.getText().toString().length()<=0
            ){
                Utils.toastinfo(LoginActivity.this,"请输入账户密码");
                return;
            }
            dologin();
        });
        binding.setting.setOnClickListener(view -> {
            NetworkDialog networkDialog = new NetworkDialog(context,activity);
            networkDialog.show();
        });
    }

    @Override
    public void initdata() {
        Api.ipaddr = getSharedPreferences("app",MODE_PRIVATE).getString("host","47.109.196.210");
        Api.port = getSharedPreferences("app",MODE_PRIVATE).getString("port","8090");
        Api.bleuuid = getSharedPreferences("app",MODE_PRIVATE).getString("bleuuid","6E400003B5A3F393E0A9E50E24DCCA9E");
        Api.seturl();
    }
}