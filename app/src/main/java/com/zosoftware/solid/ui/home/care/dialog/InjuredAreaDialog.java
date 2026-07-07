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
import com.zosoftware.solid.databinding.VoiceDialogBinding;
import com.zosoftware.solid.ui.AppDialog;
import com.zosoftware.solid.utils.UserManager;

import java.util.ArrayList;
import java.util.List;

public class InjuredAreaDialog extends AppDialog<DialogInjuredareaBinding> {


    public InjuredAreaDialog(@NonNull Context context, Activity activity) {
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
                if(binding.ckTou.isChecked())
                    results.add(binding.ckTou.getText().toString());
                if(binding.ckFubu.isChecked())
                    results.add(binding.ckFubu.getText().toString());
                if(binding.ckJingbu.isChecked())
                    results.add(binding.ckJingbu.getText().toString());
                if(binding.ckXiongbu.isChecked())
                    results.add(binding.ckXiongbu.getText().toString());
                if(binding.ckBeibu.isChecked())
                    results.add(binding.ckBeibu.getText().toString());
                if(binding.ckLeftDown.isChecked())
                    results.add(binding.ckLeftDown.getText().toString());
                if(binding.ckLeftUp.isChecked())
                    results.add(binding.ckLeftUp.getText().toString());
                if(binding.ckRightDown.isChecked())
                    results.add(binding.ckRightDown.getText().toString());
                if(binding.ckRightUp.isChecked())
                    results.add(binding.ckRightUp.getText().toString());

                if(UserManager.getCurrentUser()!=null)
                    UserManager.getCurrentUser().injured_area = results;

                if(callBack !=null)
                    callBack.doresult(results);
                dismiss();
            }
        });

        if(UserManager.getCurrentUser()!= null){
            for (String s:UserManager.getCurrentUser().injured_area) {
                if(s.contentEquals(binding.ckBeibu.getText()))
                    binding.ckBeibu.setChecked(true);
                if(s.contentEquals(binding.ckJingbu.getText()))
                    binding.ckJingbu.setChecked(true);
                if(s.contentEquals(binding.ckXiongbu.getText()))
                    binding.ckXiongbu.setChecked(true);
                if(s.contentEquals(binding.ckFubu.getText()))
                    binding.ckFubu.setChecked(true);
                if(s.contentEquals(binding.ckLeftUp.getText()))
                    binding.ckLeftUp.setChecked(true);
                if(s.contentEquals(binding.ckLeftDown.getText()))
                    binding.ckLeftDown.setChecked(true);
                if(s.contentEquals(binding.ckRightUp.getText()))
                    binding.ckRightUp.setChecked(true);
                if(s.contentEquals(binding.ckRightDown.getText()))
                    binding.ckRightDown.setChecked(true);
                if(s.contentEquals(binding.ckTou.getText()))
                    binding.ckTou.setChecked(true);
            }
        }
    }
    public void setsize() {
        getWindow().setWindowAnimations(R.style.BottomDialogAnimation);
        getWindow().setGravity(Gravity.CENTER);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels/2  + dm.widthPixels/5 ;// 屏幕宽度（像素）
        int height= dm.heightPixels; // 屏幕高度（像素）
        float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //这个地方可以用ViewGroup.LayoutParams.MATCH_PARENT属性，各位试试看看有没有效果
        layoutParams.width = width;
        layoutParams.height = height;
        getWindow().setAttributes(layoutParams);
    }
}
