package com.zosoftware.solid.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;

public class TensorFlowUtils {
    private static Interpreter tflite;

    public static float[] doInference(Context context, float[] input) throws IOException {
        if (tflite == null) {
            tflite = new Interpreter(loadModelFile(context));
        }
        float[][] output = new float[1][4]; // 修改输出数组的形状
        tflite.run(input, output);
        return output[0]; // 返回结果数组
    }

    private static MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
