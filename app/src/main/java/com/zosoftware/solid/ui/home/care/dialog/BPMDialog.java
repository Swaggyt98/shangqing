package com.zosoftware.solid.ui.home.care.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.zosoftware.solid.databinding.VoiceDialogBinding;
import com.zosoftware.solid.ui.AppDialog;

public class BPMDialog extends AppDialog<VoiceDialogBinding> {

    public BPMDialog(@NonNull Context context, Activity activity) {
        super(context, activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void init() {
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });
        if(callBack !=null)
            callBack.doresult("");
    }

}
