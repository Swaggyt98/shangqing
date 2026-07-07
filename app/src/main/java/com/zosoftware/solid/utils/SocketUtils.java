package com.zosoftware.solid.utils;

import android.bluetooth.BluetoothSocket;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import okhttp3.internal.Util;

public class SocketUtils {


    public interface SocketCallback {
        void ondata (String bytes);
    }
}
