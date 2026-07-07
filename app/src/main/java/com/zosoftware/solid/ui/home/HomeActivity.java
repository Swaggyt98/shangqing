package com.zosoftware.solid.ui.home;

import static com.zosoftware.solid.ui.home.care.dialog.VoiceDialog.REQUEST_SPEECH_INPUT;

import android.Manifest;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zosoftware.solid.R;
import com.zosoftware.solid.databinding.ActivityHomeBinding;
import com.zosoftware.solid.ui.AppActivity;
import com.zosoftware.solid.ui.AppFragment;
import com.zosoftware.solid.ui.home.care.CareFragment;
import com.zosoftware.solid.ui.home.care.dialog.VoiceDialog;
import com.zosoftware.solid.ui.home.result.ResultFragment;
import com.zosoftware.solid.ui.home.user.UserFragment;
import com.zosoftware.solid.utils.PermissionUtils;
import com.zosoftware.solid.utils.UserManager;
import com.zosoftware.solid.utils.Utils;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppActivity<ActivityHomeBinding>   implements View.OnClickListener {

    ViewPager2FragmentAdapter fragmentAdapter;
    private List<AppFragment> fragmentList =new ArrayList<>();

    UserFragment userFragment;
    CareFragment careFragment;
    ResultFragment resultFragment;

    @Override
    public void initdata() {
        userFragment = new UserFragment();
        careFragment = new CareFragment();
        resultFragment = new ResultFragment();
        fragmentList.add(userFragment);
        fragmentList.add(careFragment);
        fragmentList.add(resultFragment);
        fragmentAdapter= new ViewPager2FragmentAdapter(this,fragmentList);

        PermissionUtils.requestpermmision(activity,REQUEST_SPEECH_INPUT,new String[]{Manifest.permission.RECORD_AUDIO});
    }

    @Override
    public void init() {
        binding.viewpage.setAdapter(fragmentAdapter);
        binding.databtn.setOnClickListener(this::onClick);
        binding.resultbtn.setOnClickListener(this::onClick);
        binding.solidbtn.setOnClickListener(this::onClick);
        binding.viewpage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                swithselfragment(position);
            }
        });
        onClick(binding.databtn);
    }

    @Override
    public void onClick(View view) {
        int sel_ind = 0;
        AppFragment fragment =null;
        if(view.getId() == R.id.solidbtn) {
            sel_ind = 0;
            fragment=  new UserFragment();
        }
        if(view.getId() == R.id.databtn) {
            sel_ind=1;
            fragment=  new UserFragment();
        }
        if(view.getId() == R.id.resultbtn) {
            sel_ind =2;
            fragment=  new UserFragment();
        }
        swithselfragment(sel_ind);
        binding.viewpage.setCurrentItem(sel_ind);

    }


    static class ViewPager2FragmentAdapter extends FragmentStateAdapter{

        private final List<AppFragment> fragmentList;

        public ViewPager2FragmentAdapter(@NonNull FragmentActivity fragmentActivity, List<AppFragment> fragmentList) {
            super(fragmentActivity);
            this.fragmentList = fragmentList;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }
    }

    void swithselfragment(int id){
        binding.solidbtn.setBackgroundResource(R.drawable.nobg);
        binding.databtn.setBackgroundResource(R.drawable.nobg);
        binding.resultbtn.setBackgroundResource(R.drawable.nobg);
        if(id == 0){
            binding.solidbtn.setBackgroundResource(R.drawable.bluebg_item2);
        }
        if(id == 1){
            binding.databtn.setBackgroundResource(R.drawable.bluebg_item2);
        }
        if(id == 2){
            binding.resultbtn.setBackgroundResource(R.drawable.bluebg_item2);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserManager.stopAllThread();
        // 释放TTS资源
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_SPEECH_INPUT && grantResults.length == 0){
            Utils.toastinfo(context,"您需要授权本APP录音才可进行语音输入，谢谢!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                // 扫码结果为空，用户取消扫码
            } else {
                // 扫码成功，获取结果
                String scanContent = result.getContents();
                // 处理扫码内容
                userFragment.scancallback.doScanQRCodecallback(scanContent);
            }
        } else {
            // 处理扫码错误
        }


    }
}