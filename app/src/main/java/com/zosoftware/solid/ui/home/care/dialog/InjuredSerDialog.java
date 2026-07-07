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
import com.zosoftware.solid.databinding.DialogInjuredareaBinding;
import com.zosoftware.solid.databinding.DialogInjuredserBinding;
import com.zosoftware.solid.ui.AppDialog;
import com.zosoftware.solid.utils.UserManager;

import java.util.ArrayList;
import java.util.List;

public class InjuredSerDialog extends AppDialog<DialogInjuredserBinding> {

    public InjuredSerDialog(@NonNull Context context, Activity activity) {
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
                if(binding.ckGuanchuan.isChecked())
                    results.add(binding.ckGuanchuan.getText().toString());
                if(binding.ckNonGuanchuan.isChecked())
                    results.add(binding.ckNonGuanchuan.getText().toString());
                if(binding.ckPifu.isChecked())
                    results.add(binding.ckPifu.getText().toString());
                if(binding.ckDuanzhi.isChecked())
                    results.add(binding.ckDuanzhi.getText().toString());
                if(binding.ckChuantou.isChecked())
                    results.add(binding.ckChuantou.getText().toString());
                if(binding.ckQiexian.isChecked())
                    results.add(binding.ckQiexian.getText().toString());
                if(binding.ckGuze.isChecked())
                    results.add(binding.ckGuze.getText().toString());
                if(!binding.other.getText().toString().isEmpty())
                    results.add(binding.other.getText().toString());


                if(UserManager.getCurrentUser()!=null)
                    UserManager.getCurrentUser().injured_ser = results;

                if(callBack !=null)
                    callBack.doresult(results);
                dismiss();

            }
        });
        if(UserManager.getCurrentUser()!= null){
            for (String s : UserManager.getCurrentUser().injured_ser) {
                if(s.contentEquals(binding.ckGuanchuan.getText()))
                    binding.ckGuanchuan.setChecked(true);
                else if(s.contentEquals(binding.ckNonGuanchuan.getText()))
                    binding.ckNonGuanchuan.setChecked(true);
                else if(s.contentEquals(binding.ckPifu.getText()))
                    binding.ckPifu.setChecked(true);
                else if(s.contentEquals(binding.ckDuanzhi.getText()))
                    binding.ckDuanzhi.setChecked(true);
                else if(s.contentEquals(binding.ckChuantou.getText()))
                    binding.ckChuantou.setChecked(true);
                else if(s.contentEquals(binding.ckQiexian.getText()))
                    binding.ckQiexian.setChecked(true);
                else if(s.contentEquals(binding.ckGuze.getText()))
                    binding.ckGuze.setChecked(true);
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
