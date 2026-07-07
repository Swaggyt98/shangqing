package com.zosoftware.solid.ui.home.result.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.zosoftware.solid.bean.User;
import com.zosoftware.solid.databinding.DialogTreatmentBinding;
import com.zosoftware.solid.ui.AppDialog;
import com.zosoftware.solid.utils.UserManager;

public class TreatmentDialog extends AppDialog<DialogTreatmentBinding> {


    public TreatmentDialog(@NonNull Context context, Activity activity) {
        super(context, activity);
    }

    @Override
    public void init() {
        if(UserManager.getCurrentUser()!=null) {
            binding.antiInfection.setText(UserManager.getCurrentUser().anti_infection);
            binding.antiShock.setText(UserManager.getCurrentUser().anti_shock);
            binding.evacuation.setText(UserManager.getCurrentUser().evacuation_plan);
            binding.emergencySurgery.setText(UserManager.getCurrentUser().emergency_surgery);
        }
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });
        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UserManager.getCurrentUser()!=null) {
                    UserManager.getCurrentUser().anti_infection = binding.antiInfection.getText().toString();
                    UserManager.getCurrentUser().anti_shock = binding.antiShock.getText().toString();
                    UserManager.getCurrentUser().emergency_surgery = binding.emergencySurgery.getText().toString();
                    UserManager.getCurrentUser().evacuation_plan = binding.evacuation.getText().toString();
                }
                dismiss();
            }
        });
        if(callBack !=null)
            callBack.doresult("1");
    }
}
