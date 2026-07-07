package com.zosoftware.solid.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

public class Utils {
    private static String TAG  = " solideapp ";
    private static TextToSpeech textToSpeech;

    public static void toastinfo(Context context, String info) {
        Toast.makeText(context,info,Toast.LENGTH_SHORT).show();
        Log.d(TAG, "debug: " + info);
    }
    public static void loginfo(String info) {
        Log.d(TAG, "debug: " + info);
    }

    public static void speechInfo(String info) {

    }
}
