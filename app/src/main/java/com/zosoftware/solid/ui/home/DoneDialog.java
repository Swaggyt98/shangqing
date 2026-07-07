package com.zosoftware.solid.ui.home;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;

import com.zosoftware.solid.databinding.DonedialogBinding;
import com.zosoftware.solid.ui.AppDialog;


public class DoneDialog extends AppDialog<DonedialogBinding> {

    public DoneDialog(@NonNull Context context, Activity activity) {

        super(context, activity);
    }

    @Override
    public void init() {
        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        new Handler().postDelayed((Runnable) () -> {
            if(isShowing())
                dismiss();
        }, 3000);
    }
}
