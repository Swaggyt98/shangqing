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
import com.zosoftware.solid.databinding.DialogPhiBinding;
import com.zosoftware.solid.databinding.DialogStartBinding;
import com.zosoftware.solid.ui.AppDialog;
import com.zosoftware.solid.utils.UserManager;

public class STARTDialog extends AppDialog<DialogStartBinding> {

    public STARTDialog(@NonNull Context context, Activity activity) {
        super(context, activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setsize();
    }

    @Override
    public void init() {
        binding.cancel.setOnClickListener(view -> dismiss());
        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UserManager.getCurrentUser() != null) {
                    UserManager.getCurrentUser().is_able_to_walk = binding.w1.isChecked();
                    UserManager.getCurrentUser().is_able_to_breath = binding.b1.isChecked();
                    UserManager.getCurrentUser().is_breath_la_30 = binding.b301.isChecked();
                    UserManager.getCurrentUser().is_qidao_breath = binding.qb1.isChecked();
                    UserManager.getCurrentUser().is_maoxichongying_lg_2 = binding.m21.isChecked();
                    UserManager.getCurrentUser().mind_clear = binding.m1.isChecked();
                }
                if(callBack != null)
                    callBack.doresult(null);
                dismiss();
            }
        });
        if(UserManager.getCurrentUser() != null) {
            if(UserManager.getCurrentUser().is_able_to_walk)
                binding.w1.setChecked(true);
            else
                binding.w2.setChecked(true);

            if(UserManager.getCurrentUser().is_able_to_breath)
                binding.b1.setChecked(true);
            else
                binding.b2.setChecked(true);

            if(UserManager.getCurrentUser().is_breath_la_30)
                binding.b301.setChecked(true);
            else
                binding.b302.setChecked(true);

            if(UserManager.getCurrentUser().is_qidao_breath )
                binding.qb1.setChecked(true);
            else
                binding.qb2.setChecked(true);

            if(UserManager.getCurrentUser().is_maoxichongying_lg_2 )
                binding.m21.setChecked(true);
            else
                binding.m22.setChecked(true);

            if(UserManager.getCurrentUser().mind_clear )
                binding.m1.setChecked(true);
            else
                binding.m2.setChecked(true);
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
