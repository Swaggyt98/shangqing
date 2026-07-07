package com.zosoftware.solid.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.zosoftware.solid.utils.BindingUtil;


public abstract class AppActivity<T extends ViewBinding> extends AppCompatActivity {

    protected T binding;
    protected Context context;
    protected Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        activity = this;
        super.onCreate(savedInstanceState);
        binding = BindingUtil.inflate(getClass(), getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initdata();
        init();
    }

    // 初始化相关
    public abstract void init();
    public abstract void initdata();

    protected void click(View view, @NonNull VoidCallback callback) {
        if (view == null) {
            return;
        }
        view.setOnClickListener(v -> {
            try {
                callback.invoke();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    protected void go(Class<? extends Activity> clazz) {
        go(clazz, null);
    }

    protected void go(Class<? extends Activity> clazz, @Nullable Bundle data) {
        Intent intent = new Intent(this, clazz);
        if (data != null) {
            intent.putExtras(data);
        }
        startActivity(intent);
    }


}
