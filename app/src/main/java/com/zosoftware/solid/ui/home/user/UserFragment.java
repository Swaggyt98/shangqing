package com.zosoftware.solid.ui.home.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.zosoftware.solid.R;
import com.zosoftware.solid.bean.User;
import com.zosoftware.solid.databinding.FragmentUserBinding;
import com.zosoftware.solid.ui.AppFragment;
import com.zosoftware.solid.ui.home.care.dialog.NetworkDialog;
import com.zosoftware.solid.ui.home.device.DeviceActivity;

import com.zosoftware.solid.utils.TTSUtils;
import com.zosoftware.solid.utils.UserManager;
import com.zosoftware.solid.utils.Utils;


public class UserFragment extends AppFragment<FragmentUserBinding> implements View.OnClickListener {
    public interface Scancallback   {
        void doScanQRCodecallback(String code) ;
    }

    public static Scancallback scancallback;
    @Override
    public void init() {

        binding.blesetting.setOnClickListener( this::onClick);
        binding.adddevice.setOnClickListener( this::onClick);
        binding.rmdevice.setOnClickListener( this::onClick);
        binding.devicerecycleview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        binding.devicerecycleview.setAdapter(useradapter);
        useradapter.submitList(UserManager.userList);
        useradapter.addOnItemChildClickListener(R.id.scanusercode, (baseQuickAdapter, view, i) -> {
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.initiateScan();
            scancallback = new Scancallback() {
                @Override
                public void doScanQRCodecallback(String code) {
                    Utils.loginfo(code);
                    parseuserinfo(code,i);
                }
            };
        });

        useradapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onClick(@NonNull BaseQuickAdapter baseQuickAdapter, @NonNull View view, int i) {
                UserManager.sel_ind = i;
                Utils.loginfo("sel ind "+i);
                useradapter.submitList(UserManager.userList);
            }
        });
    }

    private void parseuserinfo(String code,int i) {
        String[] userlist = code.split("\n");
        try{
            for (String lin:userlist) {
                Utils.loginfo("line :" + lin);
                lin = lin.trim();
                if(lin.contains("名称")) {
                    UserManager.userList.get(i).username = lin.split(":")[1];
                }
                if(lin.contains("id")) {
                    UserManager.userList.get(i).userid = lin.split(":")[1];
                }
                if(lin.contains("性别")) {
                    UserManager.userList.get(i).gender = lin.split(":")[1];
                }
                if(lin.contains("血型")) {
                    UserManager.userList.get(i).bloodtype = lin.split(":")[1];
                }
                if(lin.contains("年龄")) {
                    UserManager.userList.get(i).age = lin.split(":")[1];
                }
                if(lin.contains("部别")) {
                    UserManager.userList.get(i).department = lin.split(":")[1];
                }
                if(lin.contains("职务")) {
                    UserManager.userList.get(i).duties = lin.split(":")[1];
                }
                if(lin.contains("军衔")) {
                    UserManager.userList.get(i).rank = lin.split(":")[1];
                }
            }
            Utils.loginfo("doScanQRCodecallback: "  + UserManager.userList.get(i));
            useradapter.notifyDataSetChanged();
        }catch (Exception e) {
            e.printStackTrace();
            Utils.toastinfo(context,"二维码信息错误！");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        useradapter.submitList(UserManager.userList);

        if(UserManager.userList.size()>0) {
            binding.nodevnotify.setVisibility(View.GONE);
            binding.devicerecycleview.setVisibility(View.VISIBLE);
        }else {
            binding.nodevnotify.setVisibility(View.VISIBLE);
            binding.devicerecycleview.setVisibility(View.GONE);
        }
    }

    BaseQuickAdapter useradapter = new BaseQuickAdapter<User, QuickViewHolder>() {

        @NonNull
        @Override
        protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
            return new QuickViewHolder(R.layout.useritem, viewGroup);
        }

        @SuppressLint("MissingPermission")
        @Override
        protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable User user) {
            if( UserManager.sel_ind == i) {
                ((LinearLayout) quickViewHolder.findView(R.id.topbar)).setBackgroundResource(R.drawable.bluegreenbtn);
                ((ImageView) quickViewHolder.findView(R.id.selimg)).setImageResource(R.drawable.check);
            }else {
                ((LinearLayout) quickViewHolder.findView(R.id.topbar)).setBackgroundResource(R.drawable.nobg);
                ((ImageView) quickViewHolder.findView(R.id.selimg)).setImageResource(R.drawable.round2);
            }
            if(user.device != null)
                ((TextView) quickViewHolder.findView(R.id.deviceid)).setText(user.device.getName());
            if(user.userid == null) {
                quickViewHolder.findView(R.id.connectuserinfoview).setVisibility(View.VISIBLE);
                quickViewHolder.findView(R.id.userinfo).setVisibility(View.GONE);
            }else {
                quickViewHolder.findView(R.id.connectuserinfoview).setVisibility(View.GONE);
                quickViewHolder.findView(R.id.userinfo).setVisibility(View.VISIBLE);

                ((TextView) quickViewHolder.findView(R.id.username)).setText(user.username);
                ((TextView) quickViewHolder.findView(R.id.bloodtype)).setText(user.bloodtype);
                ((TextView) quickViewHolder.findView(R.id.age)).setText(user.age);
                ((TextView) quickViewHolder.findView(R.id.userid)).setText(user.userid);

            }
            if(user.battary_val_state == 4)
                ((ImageView) quickViewHolder.findView(R.id.battery_val)).setImageResource(R.drawable.ele1);
            if(user.battary_val_state == 3)
                ((ImageView) quickViewHolder.findView(R.id.battery_val)).setImageResource(R.drawable.ele2);
            if(user.battary_val_state == 2)
                ((ImageView) quickViewHolder.findView(R.id.battery_val)).setImageResource(R.drawable.ele3);
            if(user.battary_val_state == 1)
                ((ImageView) quickViewHolder.findView(R.id.battery_val)).setImageResource(R.drawable.ele4);
            if(user.battary_val_state == 0)
                ((ImageView) quickViewHolder.findView(R.id.battery_val)).setImageResource(R.drawable.ele5);


        }
    };
    @Override
    public void initdata() {

    }

    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.blesetting){
            new NetworkDialog(context,activity).show();
            TTSUtils.shutdownTTS();
        }
        if(view.getId()== R.id.adddevice){
//            startActivity(new Intent(context, BTActivity.class));
            startActivity(new Intent(context, DeviceActivity.class));
        }
        if(view.getId()== R.id.rmdevice){
            if (UserManager.sel_ind == -1) {
                Utils.toastinfo(context,"未选择设备");
            }
            else{
                UserManager.removeCurrentUser();
                Utils.toastinfo(context,"设备移除成功");
            }
            useradapter.submitList(UserManager.userList);
        }
    }
}