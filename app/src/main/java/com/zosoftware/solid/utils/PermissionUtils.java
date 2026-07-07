package com.zosoftware.solid.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.util.List;

public class PermissionUtils {
    public static boolean requestpermmision(Activity context, int requestcode , String[] permissionlist ){
        boolean allgrand = true;
        for (String permission: permissionlist) {
            if(ContextCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED){
                allgrand = false;
            }
        }
        if (!allgrand) {
            // 权限未授予，请求权限
            Utils.loginfo("requestpermmision" +permissionlist);
            ActivityCompat.requestPermissions(context,permissionlist,requestcode);
        }
        Utils.loginfo("allgranded");
        return allgrand;
    }

    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity, String[] permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, permission, requestCode);
    }



}
