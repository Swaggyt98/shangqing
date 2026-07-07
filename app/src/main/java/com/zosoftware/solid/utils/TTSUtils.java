package com.zosoftware.solid.utils;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TTSUtils {
    private static TextToSpeech textToSpeech;
    public static void initTTS(Context context) {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = textToSpeech.setLanguage(Locale.CHINA);
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("监测异常报警", "TTS 语言不支持");
                        } else {
                            Log.i("监测异常报警", "TTS 初始化成功");
                        }
                    } else {
                        Log.e("监测异常报警", "TextToSpeech 初始化失败，状态码：" + status);
                    }
                }
            });
        }
    }
    public static void speakText(String text, float speechRate, float speechPitch) {
//        // 确保在主线程上执行
//        if (Looper.myLooper() != Looper.getMainLooper()) {
//            new Handler(Looper.getMainLooper()).post(() -> speakText(text, speechRate, speechPitch));
//            return;
//        }
        if (textToSpeech != null) {
            textToSpeech.setSpeechRate(speechRate); // 设置语速
            textToSpeech.setPitch(speechPitch); // 设置语调
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            Utils.loginfo("已播报："+text);
        } else {
            Log.e("监测异常报警", "TextToSpeech 未初始化");
        }
    }

    public static void shutdownTTS() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
            Log.d("监测异常报警", "TextToSpeech 已关闭");
        }
    }
}
