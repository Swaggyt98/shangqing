package com.zosoftware.solid.ui.home.care.dialog;
//这个 GCSDialog 类是一个自定义的对话框，用于展示和选择与 GCS（Glasgow Coma Scale）相关的评分项。GCSDialog 继承自 AppDialog 类，并使用 DialogGcsBinding 进行布局绑定。
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
import com.zosoftware.solid.databinding.DialogInjuredareaBinding;
import com.zosoftware.solid.ui.AppDialog;
import com.zosoftware.solid.utils.UserManager;
import com.zosoftware.solid.utils.Utils;

public class GCSDialog extends AppDialog<DialogGcsBinding> {

    public GCSDialog(@NonNull Context context, Activity activity) {
        super(context, activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void init() {
        setsize();
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.confirm.setOnClickListener(view -> {
            if(UserManager.getCurrentUser()!=null){
                if(binding.y1.isChecked())
                    UserManager.getCurrentUser().zhengyan_ability = 1;
                if(binding.y2.isChecked())
                    UserManager.getCurrentUser().zhengyan_ability = 2;
                if(binding.y3.isChecked())
                    UserManager.getCurrentUser().zhengyan_ability = 3;
                if(binding.y4.isChecked())
                    UserManager.getCurrentUser().zhengyan_ability = 4;


                if(binding.l1.isChecked())
                    UserManager.getCurrentUser().language_ability = 1;
                if(binding.l2.isChecked())
                    UserManager.getCurrentUser().language_ability = 2;
                if(binding.l3.isChecked())
                    UserManager.getCurrentUser().language_ability = 3;
                if(binding.l4.isChecked())
                    UserManager.getCurrentUser().language_ability = 4;
                if(binding.l5.isChecked())
                    UserManager.getCurrentUser().language_ability = 5;


                if(binding.s1.isChecked())
                    UserManager.getCurrentUser().sport_ability = 1;
                if(binding.s2.isChecked())
                    UserManager.getCurrentUser().sport_ability = 2;
                if(binding.s3.isChecked())
                    UserManager.getCurrentUser().sport_ability = 3;
                if(binding.s4.isChecked())
                    UserManager.getCurrentUser().sport_ability = 4;
                if(binding.s5.isChecked())
                    UserManager.getCurrentUser().sport_ability = 5;
                if(binding.s6.isChecked())
                    UserManager.getCurrentUser().sport_ability = 6;
                UserManager.getCurrentUser().GCS_score =
                        UserManager.getCurrentUser().zhengyan_ability +
                                UserManager.getCurrentUser().sport_ability +
                                UserManager.getCurrentUser().language_ability;

                if(callBack !=null)
                    callBack.doresult(( UserManager.getCurrentUser().GCS_score )+"");

            }
            dismiss();
        });
        if(UserManager.getCurrentUser()!=null){

            if(UserManager.getCurrentUser().zhengyan_ability == 1)
                binding.y1.setChecked(true);
            if(UserManager.getCurrentUser().zhengyan_ability == 2)
                binding.y2.setChecked(true);
            if(UserManager.getCurrentUser().zhengyan_ability == 3)
                binding.y3.setChecked(true);
            if(UserManager.getCurrentUser().zhengyan_ability == 4)
                binding.y4.setChecked(true);

            if(UserManager.getCurrentUser().language_ability == 1)
                binding.l1.setChecked(true);
            if(UserManager.getCurrentUser().language_ability == 2)
                binding.l2.setChecked(true);
            if(UserManager.getCurrentUser().language_ability == 3)
                binding.l3.setChecked(true);
            if(UserManager.getCurrentUser().language_ability == 4)
                binding.l4.setChecked(true);
            if(UserManager.getCurrentUser().language_ability == 5)
                binding.l5.setChecked(true);


            if(UserManager.getCurrentUser().sport_ability == 1)
                binding.s1.setChecked(true);
            if(UserManager.getCurrentUser().sport_ability == 2)
                binding.s2.setChecked(true);
            if(UserManager.getCurrentUser().sport_ability == 3)
                binding.s3.setChecked(true);
            if(UserManager.getCurrentUser().sport_ability == 4)
                binding.s4.setChecked(true);
            if(UserManager.getCurrentUser().sport_ability == 5)
                binding.s5.setChecked(true);
            if(UserManager.getCurrentUser().sport_ability == 6)
                binding.s6.setChecked(true);
        }
    }
    public void setsize() {
        getWindow().setWindowAnimations(R.style.BottomDialogAnimation);
        getWindow().setGravity(Gravity.CENTER);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels/2 + dm.widthPixels/4 ;// 屏幕宽度（像素）
        int height= dm.heightPixels/2+ dm.heightPixels/3 + dm.heightPixels/20 ; // 屏幕高度（像素）
        float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //这个地方可以用ViewGroup.LayoutParams.MATCH_PARENT属性，各位试试看看有没有效果
        layoutParams.width = width;
        layoutParams.height = height;
        getWindow().setAttributes(layoutParams);
    }
}
