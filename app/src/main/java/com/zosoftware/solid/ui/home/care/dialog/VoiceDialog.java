package com.zosoftware.solid.ui.home.care.dialog;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.zosoftware.solid.R;
import com.zosoftware.solid.bean.User;
import com.zosoftware.solid.databinding.VoiceDialogBinding;
import com.zosoftware.solid.ui.AppDialog;

import com.zosoftware.solid.utils.PermissionUtils;
import com.zosoftware.solid.utils.UserManager;
import com.zosoftware.solid.utils.Utils;
import com.zosoftware.solid.utils.asr.ise.ASR;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.internal.Util;

public class VoiceDialog extends AppDialog<VoiceDialogBinding> {
    public static final int REQUEST_SPEECH_INPUT = 12890;

    List<String> comprehand_comman = new ArrayList<>();
    List<String> area_comman = new ArrayList<>();
    List<String> type_comman = new ArrayList<>();
    List<String> ser_comman = new ArrayList<>();

    List<String> degrea_comman = new ArrayList<>();

    public VoiceDialog(@NonNull Context context, Activity activity) {
        super(context, activity);
        setwindowsize2();
        initcommantype();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                ASR.stop();
                ASR.cancel();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingPermission")
    BaseQuickAdapter degreaAdapter = new BaseQuickAdapter<String, QuickViewHolder>() {
        @NonNull
        @Override
        protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
            return new QuickViewHolder(R.layout.item_checkbox, viewGroup);
        }
        @Override
        protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable String objects) {
            ((CheckBox) quickViewHolder.findView(R.id.checkbox)).setText(objects);
            if(UserManager.getCurrentUser() != null) {
                if(UserManager.getCurrentUser().injured_severity.equals(objects)) {
                    ((CheckBox) quickViewHolder.findView(R.id.checkbox)).setChecked(true);
                }else {
                    ((CheckBox) quickViewHolder.findView(R.id.checkbox)).setChecked(false);
                }
            }
        }
    };
    @SuppressLint("MissingPermission")
    BaseQuickAdapter injuredareaAdapter = new BaseQuickAdapter<String, QuickViewHolder>() {
        @NonNull
        @Override
        protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
            return new QuickViewHolder(R.layout.item_checkbox, viewGroup);
        }
        @Override
        protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable String objects) {
            ((CheckBox) quickViewHolder.findView(R.id.checkbox)).setText(objects);
            if(UserManager.getCurrentUser() != null) {
                if(UserManager.getCurrentUser().injured_area.contains(objects.toString())) {
                    ((CheckBox) quickViewHolder.findView(R.id.checkbox)).setChecked(true);
                }else {
                    ((CheckBox) quickViewHolder.findView(R.id.checkbox)).setChecked(false);
                }
            }
        }
    };
    @SuppressLint("MissingPermission")
    BaseQuickAdapter comprehandAdapter = new BaseQuickAdapter<String, QuickViewHolder>() {
        @NonNull
        @Override
        protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
            return new QuickViewHolder(R.layout.item_checkbox, viewGroup);
        }
        @Override
        protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable String objects) {
            ((CheckBox) quickViewHolder.findView(R.id.checkbox)).setText(objects);
            if(UserManager.getCurrentUser() != null) {
                if(UserManager.getCurrentUser().comprehan.contains(objects.toString())) {
                    ((CheckBox) quickViewHolder.findView(R.id.checkbox)).setChecked(true);
                }else {
                    ((CheckBox) quickViewHolder.findView(R.id.checkbox)).setChecked(false);
                }
            }
        }
    };

    @SuppressLint("MissingPermission")
    BaseQuickAdapter typeAdapter = new BaseQuickAdapter<String, QuickViewHolder>() {
        @NonNull
        @Override
        protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
            return new QuickViewHolder(R.layout.item_checkbox, viewGroup);
        }
        @Override
        protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable String objects) {
            ((CheckBox) quickViewHolder.findView(R.id.checkbox)).setText(objects);
            if(UserManager.getCurrentUser() != null) {
                if(UserManager.getCurrentUser().injured_type.contains(objects.toString())) {
                    ((CheckBox) quickViewHolder.findView(R.id.checkbox)).setChecked(true);
                }else {
                    ((CheckBox) quickViewHolder.findView(R.id.checkbox)).setChecked(false);
                }
            }
        }
    };


    @SuppressLint("MissingPermission")
    BaseQuickAdapter injuredserAdapter = new BaseQuickAdapter<String, QuickViewHolder>() {
        @NonNull
        @Override
        protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
            return new QuickViewHolder(R.layout.item_checkbox, viewGroup);
        }
        @Override
        protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable String objects) {
            ((CheckBox) quickViewHolder.findView(R.id.checkbox)).setText(objects);
            if(UserManager.getCurrentUser() != null) {
                if(UserManager.getCurrentUser().injured_ser.contains(objects.toString())) {
                    ((CheckBox) quickViewHolder.findView(R.id.checkbox)).setChecked(true);
                }else {
                    ((CheckBox) quickViewHolder.findView(R.id.checkbox)).setChecked(false);
                }
            }
        }
    };


    @Override
    public void init() {

        binding.addcomprehan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        binding.cancel.setOnClickListener(view -> dismiss());
        binding.voiceinput.setOnTouchListener((view, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 按钮被按下
                    initiateVoiceRecognition();
                    view.setPressed(true);
                    break;
                case MotionEvent.ACTION_UP:
                    // 按钮被抬起
                    ASR.stop();
                    view.setPressed(false);
                    break;
            }
            return true;
        });
        binding.comprehanRecy.setLayoutManager(new FlexboxLayoutManager(context));
        binding.comprehanRecy.setAdapter(comprehandAdapter);
        comprehandAdapter.submitList(comprehand_comman);


        binding.injuredarea.setLayoutManager(new FlexboxLayoutManager(context));
        binding.injuredarea.setAdapter(injuredareaAdapter);
        injuredareaAdapter.submitList(area_comman);

        binding.injuredtype.setLayoutManager(new FlexboxLayoutManager(context));
        binding.injuredtype.setAdapter(typeAdapter);
        typeAdapter.submitList(type_comman);

        binding.injuredser.setLayoutManager(new FlexboxLayoutManager(context));
        binding.injuredser.setAdapter(injuredserAdapter);
        injuredserAdapter.submitList(ser_comman);

        binding.injureddeg.setLayoutManager(new FlexboxLayoutManager(context));
        binding.injureddeg.setAdapter(degreaAdapter);
        degreaAdapter.submitList(degrea_comman);




        binding.addinjuredarea.setOnClickListener(view -> showinputdialog("伤部"));
        binding.addinjuredser.setOnClickListener(view -> showinputdialog("伤类"));
        binding.addinjuredtype.setOnClickListener(view -> showinputdialog("伤型"));
        binding.addcomprehan.setOnClickListener(view -> showinputdialog("并发症"));
        binding.confirm.setOnClickListener(view -> {
            dismiss();
            if(callBack != null)
                callBack.doresult("");
        });
        binding.input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                dealwithresult();
            }
        });
    }

    private void dealwithresult() {

        for (String s: comprehand_comman) {
            if(UserManager.getCurrentUser()!= null){
                if(binding.input.getText().toString().contains(s)&& !UserManager.getCurrentUser().comprehan.contains(s) ) {
                    UserManager.getCurrentUser().comprehan.add(s);
                }
            }
        }
        for (String s: area_comman) {
            if(UserManager.getCurrentUser()!= null){
                if(binding.input.getText().toString().contains(s)&& !UserManager.getCurrentUser().injured_area.contains(s) ) {
                    UserManager.getCurrentUser().injured_area.add(s);
                }
            }
        }
        for (String s: type_comman) {
            if(UserManager.getCurrentUser()!= null){
                if(binding.input.getText().toString().contains(s)&& !UserManager.getCurrentUser().injured_type.contains(s) ) {
                    UserManager.getCurrentUser().injured_type.add(s);
                }
            }
        }
        for (String s: ser_comman) {
            if(UserManager.getCurrentUser()!= null){
                if(binding.input.getText().toString().contains(s)&& !UserManager.getCurrentUser().injured_ser.contains(s) ) {
                    UserManager.getCurrentUser().injured_ser.add(s);
                }
            }
        }
        for (String s: degrea_comman) {
            if(UserManager.getCurrentUser()!= null){
                if(binding.input.getText().toString().contains(s)&& !UserManager.getCurrentUser().injured_severity.equals(s) ) {
                    UserManager.getCurrentUser().injured_severity = s;
                }
            }
        }
        degreaAdapter.submitList(degrea_comman);
        injuredareaAdapter.submitList(area_comman);
        injuredserAdapter.submitList(ser_comman);
        typeAdapter.submitList(type_comman);
        comprehandAdapter.submitList(comprehand_comman);
    }

    public void showinputdialog(String type){
        EditText editText = new EditText(context);
        Dialog alertDialog = new AlertDialog.Builder(context).
                setTitle("输入自定义：" + type).
                setView(editText).
                setPositiveButton("确认", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String val = editText.getText().toString();
                        if(!val.isEmpty()){
                            if (Objects.equals(type, "伤部")) {
                                area_comman.add(val);
                                if(UserManager.getCurrentUser()!= null)  UserManager.getCurrentUser().injured_area.add(val);
                                injuredareaAdapter.submitList(area_comman);
                            }
                            if (Objects.equals(type, "伤型")) {
                                type_comman.add(val);
                                if(UserManager.getCurrentUser()!= null)  UserManager.getCurrentUser().injured_type.add(val);
                                typeAdapter.submitList(type_comman);
                            }
                            if (Objects.equals(type, "伤类")) {
                                ser_comman.add(val);
                                if(UserManager.getCurrentUser()!= null)  UserManager.getCurrentUser().injured_ser.add(val);
                                injuredserAdapter.submitList(ser_comman);
                            }
                            if (Objects.equals(type, "并发症")) {
                                comprehand_comman.add(val);
                                if(UserManager.getCurrentUser()!= null)  UserManager.getCurrentUser().comprehan.add(val);
                                comprehandAdapter.submitList(comprehand_comman);
                            }
                        }
                    }
                }).setNegativeButton("取消",null).
                create();

        alertDialog.show();
    }




    private void initiateVoiceRecognition() {

// 创建RecognizerSetting对象，并设置一些参数

        Utils.loginfo("start asr");
        ASR.asrCallBack = result -> activity.runOnUiThread(() -> {
            if(result != null && !result.trim().isEmpty()){
                binding.input.setText( binding.input.getText().toString() + result);
            }
        });
        ASR.dovoicereco(context);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ASR.cancel();
    }


    private void initcommantype() {
        comprehand_comman.add("大出血");
        comprehand_comman.add("休克");
        comprehand_comman.add("气胸");
        comprehand_comman.add("气性坏疽");
        comprehand_comman.add("窒息");
        comprehand_comman.add("抽搐");
        comprehand_comman.add("截瘫");


        type_comman.add("炸伤");
        type_comman.add("刃器伤");
        type_comman.add("冲击伤");
        type_comman.add("烧伤");
        type_comman.add("毒剂伤");
        type_comman.add("生物武器伤");
        type_comman.add("微波损伤");
        type_comman.add("枪弹伤");
        type_comman.add("挤压伤");
        type_comman.add("撞击伤");
        type_comman.add("冻伤");
        type_comman.add("电离辐射伤");
        type_comman.add("激光损伤");
        type_comman.add("复合伤");


        ser_comman.add("贯通伤");
        ser_comman.add("非贯通伤");
        ser_comman.add("皮肤及软组织伤");
        ser_comman.add("断肢和断指");
        ser_comman.add("穿透伤");
        ser_comman.add("切线伤");
        ser_comman.add("骨折");


        degrea_comman.add("轻伤");
        degrea_comman.add("中度伤");
        degrea_comman.add("重伤");
        degrea_comman.add("危重伤");

        area_comman.add("头部");
        area_comman.add("背部");
        area_comman.add("颈部");
        area_comman.add("胸部");
        area_comman.add("右上肢");
        area_comman.add("右下肢");
        area_comman.add("腹部");
        area_comman.add("左上肢");
        area_comman.add("左下肢");

        if(UserManager.getCurrentUser()!=null){
            for (String s:UserManager.getCurrentUser().injured_ser) {
                if(!ser_comman.contains(s))
                    ser_comman.add(s);
            }
            for (String s:UserManager.getCurrentUser().injured_area) {
                if(!area_comman.contains(s))
                    area_comman.add(s);
            }
            for (String s:UserManager.getCurrentUser().injured_type) {
                if(!type_comman.contains(s))
                    type_comman.add(s);
            }
            for (String s:UserManager.getCurrentUser().comprehan) {
                if(!comprehand_comman.contains(s))
                    comprehand_comman.add(s);
            }
        }
    }


}
