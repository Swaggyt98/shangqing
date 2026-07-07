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
import com.zosoftware.solid.bean.User;
import com.zosoftware.solid.databinding.DialogGcsBinding;
import com.zosoftware.solid.databinding.DialogPhiBinding;
import com.zosoftware.solid.ui.AppDialog;
import com.zosoftware.solid.utils.UserManager;
import com.zosoftware.solid.utils.Utils;

import okhttp3.internal.Util;

public class PHIDialog extends AppDialog<DialogPhiBinding> {

    public PHIDialog(@NonNull Context context, Activity activity) {
        super(context, activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void init() {
        setsize();
        binding.cancel.setOnClickListener(view -> dismiss());
        binding.confirm.setOnClickListener(view -> {

            int score =0;
            try{
                int mmhg = 0 ;
                int hr = Integer.parseInt(binding.hr.getText().toString());
                if(binding.mmhg.getText().toString().contains("/")){
                    mmhg = Integer.parseInt(binding.mmhg.getText().toString().split("/")[1]);
                }
                int bpm = Integer.parseInt(binding.bpm.getText().toString());
                if(hr<=119 && hr >=51)
                    score +=0;
                if(hr>=120)
                    score +=3;
                if(hr<=50)
                    score +=5;

                if( mmhg >100)
                    score +=0;
                if(mmhg<=100 && mmhg >=86)
                    score +=1;
                if(mmhg<=85 && mmhg >=75)
                    score +=2;
                if(mmhg<=74 && mmhg >=0)
                    score +=3;

                if(binding.m2.isChecked())
                    score +=3;
                if(binding.m3.isChecked())
                    score +=5;
                if(binding.e2.isChecked())
                    score +=4;


                if(UserManager.getCurrentUser()!=null){
                    UserManager.getCurrentUser().is_xiongbu_guanchuan = binding.e2.isChecked() ;
                    if(binding.m1.isChecked())
                        UserManager.getCurrentUser().mind_state = 0;
                    if(binding.m2.isChecked())
                        UserManager.getCurrentUser().mind_state = 1;
                    if(binding.m3.isChecked())
                        UserManager.getCurrentUser().mind_state = 2;
                    UserManager.getCurrentUser().PHI_score =  score;
                    if(callBack !=null)
                        callBack.doresult(score+"");
                }
                dismiss();

            }catch (Exception e){
                Utils.toastinfo(context,"分析错误，请检查");
            }



        });
        if(UserManager.getCurrentUser()!=null){

            binding.bpm.setText(UserManager.getCurrentUser().hr);
            binding.hr.setText(UserManager.getCurrentUser().hr);
            binding.mmhg.setText(UserManager.getCurrentUser().npb);
            if(UserManager.getCurrentUser().is_xiongbu_guanchuan){
                binding.e2.setChecked(true);
            }else
                binding.e1.setChecked(true);
            if(UserManager.getCurrentUser().mind_state == 0 )
                binding.m1.setChecked(true);
            if(UserManager.getCurrentUser().mind_state == 1 )
                binding.m2.setChecked(true);
            if(UserManager.getCurrentUser().mind_state == 2 )
                binding.m3.setChecked(true);
        }
    }

    public void setsize() {
        getWindow().setWindowAnimations(R.style.BottomDialogAnimation);
        getWindow().setGravity(Gravity.CENTER);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels/2 + dm.widthPixels/4 ;// 屏幕宽度（像素）
        int height= dm.heightPixels/2+ dm.heightPixels/3  ; // 屏幕高度（像素）
        float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //这个地方可以用ViewGroup.LayoutParams.MATCH_PARENT属性，各位试试看看有没有效果
        layoutParams.width = width;
        layoutParams.height = height;
        getWindow().setAttributes(layoutParams);
    }
}
