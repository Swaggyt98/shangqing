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
import com.zosoftware.solid.databinding.DialogInjuredtypeBinding;
import com.zosoftware.solid.ui.AppDialog;
import com.zosoftware.solid.utils.UserManager;

import java.util.ArrayList;
import java.util.List;

public class InjuredTypeDialog extends AppDialog<DialogInjuredtypeBinding> {


    public InjuredTypeDialog(@NonNull Context context, Activity activity) {
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
                if(binding.ckZhashang.isChecked())
                    results.add(binding.ckZhashang.getText().toString());
                if(binding.ckRunqi.isChecked())
                    results.add(binding.ckRunqi.getText().toString());
                if(binding.ckChongji.isChecked())
                    results.add(binding.ckChongji.getText().toString());
                if(binding.ckShaoshang.isChecked())
                    results.add(binding.ckShaoshang.getText().toString());
                if(binding.ckDuji.isChecked())
                    results.add(binding.ckDuji.getText().toString());
                if(binding.ckShengwu.isChecked())
                    results.add(binding.ckShengwu.getText().toString());
                if(binding.ckWeibo.isChecked())
                    results.add(binding.ckWeibo.getText().toString());
                if(binding.ckQiangdan.isChecked())
                    results.add(binding.ckQiangdan.getText().toString());
                if(binding.ckJiya.isChecked())
                    results.add(binding.ckJiya.getText().toString());
                if(binding.ckZhuangji.isChecked())
                    results.add(binding.ckZhuangji.getText().toString());
                if(binding.ckDongshang.isChecked())
                    results.add(binding.ckDongshang.getText().toString());
                if(binding.ckDianlifuse.isChecked())
                    results.add(binding.ckDianlifuse.getText().toString());
                if(binding.ckJiguang.isChecked())
                    results.add(binding.ckJiguang.getText().toString());
                if(binding.ckFuhe.isChecked())
                    results.add(binding.ckFuhe.getText().toString());



                if(!binding.ckOther.getText().toString().isEmpty())
                    results.add(binding.ckOther.getText().toString());


                if(UserManager.getCurrentUser()!=null)
                    UserManager.getCurrentUser().injured_type = results;
                if(callBack !=null)
                    callBack.doresult(results);
                dismiss();

            }
        });
        if(UserManager.getCurrentUser()!= null){
            for (String s : UserManager.getCurrentUser().injured_type) {
                if(s.contentEquals(binding.ckZhashang.getText()))
                    binding.ckZhashang.setChecked(true);
                else if(s.contentEquals(binding.ckRunqi.getText()))
                    binding.ckRunqi.setChecked(true);
                else if(s.contentEquals(binding.ckChongji.getText()))
                    binding.ckChongji.setChecked(true);
                else if(s.contentEquals(binding.ckShaoshang.getText()))
                    binding.ckShaoshang.setChecked(true);
                else if(s.contentEquals(binding.ckDuji.getText()))
                    binding.ckDuji.setChecked(true);
                else if(s.contentEquals(binding.ckShengwu.getText()))
                    binding.ckShengwu.setChecked(true);
                else if(s.contentEquals(binding.ckWeibo.getText()))
                    binding.ckWeibo.setChecked(true);
                else if(s.contentEquals(binding.ckQiangdan.getText()))
                    binding.ckQiangdan.setChecked(true);
                else if(s.contentEquals(binding.ckJiya.getText()))
                    binding.ckJiya.setChecked(true);
                else if(s.contentEquals(binding.ckZhuangji.getText()))
                    binding.ckZhuangji.setChecked(true);
                else if(s.contentEquals(binding.ckDongshang.getText()))
                    binding.ckDongshang.setChecked(true);
                else if(s.contentEquals(binding.ckDianlifuse.getText()))
                    binding.ckDianlifuse.setChecked(true);
                else if(s.contentEquals(binding.ckJiguang.getText()))
                    binding.ckJiguang.setChecked(true);
                else if(s.contentEquals(binding.ckFuhe.getText()))
                    binding.ckFuhe.setChecked(true);
                else
                    binding.ckOther.setText(s);
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
