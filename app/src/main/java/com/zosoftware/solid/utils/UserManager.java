package com.zosoftware.solid.utils;

import android.bluetooth.BluetoothDevice;

import com.zosoftware.solid.bean.User;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    public interface Datacallback {
        void dealdata(float ecg);
    }
    public static Datacallback datacallback ;
    public static int sel_ind = -1;
    public static List<User> userList = new ArrayList<>();

    public static void stopAllThread() {
        for (User u: userList) {
            u.stop = true;
            u.closeThread();
        }
    }

    public static User getCurrentUser(){
        if (sel_ind == -1)
            return null;
        else
            return userList.get(sel_ind);
    }
    public static void disconnectdev(BluetoothDevice device){

        for (int i =0;i<userList.size();i++) {
            if(userList.get(i).device.getAddress().equals(device.getAddress())){
                if(sel_ind == i)
                    sel_ind=-1;
                userList.remove(i);
                break;
            }
        }
    }

    public static void removeCurrentUser(){
        if (sel_ind != -1){
            userList.get(sel_ind).disConnGatt();
            userList.remove(sel_ind);
            sel_ind = -1;
        }
    }
}
