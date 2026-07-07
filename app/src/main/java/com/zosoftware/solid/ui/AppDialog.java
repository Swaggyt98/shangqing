package com.zosoftware.solid.ui;
//AppDialog 类是一个灵活的对话框基类，可以根据需要调整对话框的大小和样式。它定义了一些方法用于设置对话框的大小（setfullwindow, setwindowsize2, setbgwindow, setsmallwindow），
// 并提供了一个抽象的 init 方法，让子类实现具体的初始化逻辑。该类还提供了便利方法来处理点击事件和启动新的 Activity。

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.zosoftware.solid.R;
import com.zosoftware.solid.utils.BindingUtil;


public abstract class AppDialog<T extends ViewBinding> extends Dialog {

    public interface DialogCallBack {
        public void doresult(Object result);
    }
    public DialogCallBack callBack;
    protected T binding;
    protected Context context;
    protected Activity activity;

    public AppDialog(@NonNull Context context,Activity activity) {
        super(context);
        this.activity= activity;
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BindingUtil.inflate(getClass(), getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);
        setfullwindow();
        init();
    }

    public void setfullwindow() {
        getWindow().setWindowAnimations(R.style.BottomDialogAnimation);
        getWindow().setGravity(Gravity.CENTER);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels-100;// 屏幕宽度（像素）
        int height= dm.heightPixels-100; // 屏幕高度（像素）
        float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //这个地方可以用ViewGroup.LayoutParams.MATCH_PARENT属性，各位试试看看有没有效果
        layoutParams.width = width;
        layoutParams.height = height;
        getWindow().setAttributes(layoutParams);
    }

    public void setwindowsize2() {
        getWindow().setWindowAnimations(R.style.BottomDialogAnimation);
        getWindow().setGravity(Gravity.CENTER);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels-50;// 屏幕宽度（像素）
        int height= dm.heightPixels-50; // 屏幕高度（像素）
        float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //这个地方可以用ViewGroup.LayoutParams.MATCH_PARENT属性，各位试试看看有没有效果
        layoutParams.width = width;
        layoutParams.height = height;
        getWindow().setAttributes(layoutParams);
    }

    public void setbgwindow() {
        getWindow().setWindowAnimations(R.style.BottomDialogAnimation);
        getWindow().setGravity(Gravity.CENTER);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels-300;// 屏幕宽度（像素）
        int height= dm.heightPixels-300; // 屏幕高度（像素）
        float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //这个地方可以用ViewGroup.LayoutParams.MATCH_PARENT属性，各位试试看看有没有效果
        layoutParams.width = width;
        layoutParams.height = height;
        getWindow().setAttributes(layoutParams);
    }
    public void setsmallwindow() {
        getWindow().setWindowAnimations(R.style.BottomDialogAnimation);
        getWindow().setGravity(Gravity.CENTER);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels/2;// 屏幕宽度（像素）
        int height= dm.heightPixels/2 + dm.heightPixels/10; // 屏幕高度（像素）
        float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //这个地方可以用ViewGroup.LayoutParams.MATCH_PARENT属性，各位试试看看有没有效果
        layoutParams.width = width;
        layoutParams.height = height;
        getWindow().setAttributes(layoutParams);
    }


    // 初始化相关
    public abstract void init();

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
        Intent intent = new Intent(activity, clazz);
        if (data != null) {
            intent.putExtras(data);
        }
        activity.startActivity(intent);
    }


}
