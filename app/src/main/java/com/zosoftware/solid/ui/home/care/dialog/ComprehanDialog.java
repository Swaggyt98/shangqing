package com.zosoftware.solid.ui.home.care.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.zosoftware.solid.R;
import com.zosoftware.solid.databinding.DialogComprehanBinding;
import com.zosoftware.solid.databinding.DialogInjuredserBinding;
import com.zosoftware.solid.ui.AppDialog;
import com.zosoftware.solid.utils.UserManager;

import java.util.ArrayList;
import java.util.List;

public class ComprehanDialog extends AppDialog<DialogComprehanBinding> {

    public ComprehanDialog(@NonNull Context context, Activity activity) {
        super(context, activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setsize();
    }

    @Override
    public void init() {
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<String> results = new ArrayList<>();
                if(binding.ckDachuxue.isChecked())
                    results.add(binding.ckDachuxue.getText().toString());
                if(binding.ckXiuke.isChecked())
                    results.add(binding.ckXiuke.getText().toString());
                if(binding.ckQixiong.isChecked())
                    results.add(binding.ckQixiong.getText().toString());
                if(binding.ckQixinghuai.isChecked())
                    results.add(binding.ckQixinghuai.getText().toString());
                if(binding.ckZhixi.isChecked())
                    results.add(binding.ckZhixi.getText().toString());
                if(binding.ckChoucu.isChecked())
                    results.add(binding.ckChoucu.getText().toString());
                if(binding.ckJiezhi.isChecked())
                    results.add(binding.ckJiezhi.getText().toString());

                if(!binding.other.getText().toString().isEmpty())
                    results.add(binding.other.getText().toString());


                if(UserManager.getCurrentUser()!=null)
                    UserManager.getCurrentUser().comprehan = results;
                callBack.doresult(results);
                dismiss();

            }
        });
        if(UserManager.getCurrentUser()!= null){
            for (String s : UserManager.getCurrentUser().comprehan) {
                if(s.contentEquals(binding.ckDachuxue.getText()))
                    binding.ckDachuxue.setChecked(true);
                else if(s.contentEquals(binding.ckXiuke.getText()))
                    binding.ckXiuke.setChecked(true);
                else if(s.contentEquals(binding.ckQixiong.getText()))
                    binding.ckQixiong.setChecked(true);
                else if(s.contentEquals(binding.ckQixinghuai.getText()))
                    binding.ckQixinghuai.setChecked(true);
                else if(s.contentEquals(binding.ckZhixi.getText()))
                    binding.ckZhixi.setChecked(true);
                else if(s.contentEquals(binding.ckChoucu.getText()))
                    binding.ckChoucu.setChecked(true);
                else if(s.contentEquals(binding.ckJiezhi.getText()))
                    binding.ckJiezhi.setChecked(true);
                else
                    binding.other.setText(s);
            }
        }



    }
    public void setsize() {
        getWindow().setWindowAnimations(R.style.BottomDialogAnimation);
        getWindow().setGravity(Gravity.CENTER);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels/2  ;// 屏幕宽度（像素）
        int height= dm.heightPixels/2+ dm.heightPixels/5 ; // 屏幕高度（像素）
        float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //这个地方可以用ViewGroup.LayoutParams.MATCH_PARENT属性，各位试试看看有没有效果
        layoutParams.width = width;
        layoutParams.height = height;
        getWindow().setAttributes(layoutParams);
    }
}
