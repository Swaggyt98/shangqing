package com.zosoftware.solid.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.zosoftware.solid.utils.BindingUtil;


public abstract class AppFragment<T extends ViewBinding> extends Fragment {

    protected T binding;
    protected Context context;
    protected Activity activity;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = BindingUtil.inflate(getClass(), getLayoutInflater());
        context = getActivity();
        activity = getActivity();
        init();
        initdata();
        return binding.getRoot();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // 初始化相关
    public abstract void init();
    public abstract void initdata();
    protected void go(Class<? extends Activity> clazz) {
        go(clazz, null);
    }
    protected void go(Class<? extends Activity> clazz, @Nullable Bundle data) {
        Intent intent = new Intent(context, clazz);
        if (data != null) {
            intent.putExtras(data);
        }
        startActivity(intent);
    }
}
