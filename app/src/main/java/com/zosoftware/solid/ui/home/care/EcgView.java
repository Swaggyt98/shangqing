package com.zosoftware.solid.ui.home.care;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zosoftware.solid.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class EcgView extends View {

    private Paint paint;
    private List<Float> ecgData = new ArrayList<>();
    private float maxEcgValue = Float.MIN_VALUE;
    private float minEcgValue = Float.MAX_VALUE;

    public EcgView(Context context) {
        super(context);
        init();
    }

    public EcgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE); // 设置画笔颜色
        paint.setStrokeWidth(2f);   // 设置线条宽度
    }

    // 添加心电数据
    public void addEcgData(float ecgValue) {
        // 缩小 200 倍并加 500
        ecgValue = ecgValue  ;

        ecgData.add(ecgValue);

        // 更新最大值和最小值
        if (ecgValue > maxEcgValue) {
            maxEcgValue = ecgValue;
        }
        if (ecgValue < minEcgValue) {
            minEcgValue = ecgValue;
        }

        Utils.loginfo("Data size: " + ecgData.size());
        Utils.loginfo("Current value: " + ecgValue);

        invalidate(); // 刷新视图
    }

    // 清除心电数据
    public void clearEcgData() {
        ecgData.clear();
        maxEcgValue = Float.MIN_VALUE;
        minEcgValue = Float.MAX_VALUE;
        invalidate(); // 刷新视图
    }

   @Override

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (ecgData.size() < 2) {
            return;
        }

        float viewHeight = getHeight();
        float viewWidth = getWidth();

        // 计算比例因子
        float valueRange = maxEcgValue - minEcgValue;
        float scale = viewHeight / valueRange;

        // 绘制心电图线
        for (int i = 0; i < ecgData.size() - 1; i++) {
            float x1 = i * (viewWidth / (ecgData.size() - 1));
            float y1 = viewHeight - ((ecgData.get(i) - minEcgValue) * scale);
            float x2 = (i + 1) * (viewWidth / (ecgData.size() - 1));
            float y2 = viewHeight - ((ecgData.get(i + 1) - minEcgValue) * scale);
            canvas.drawLine(x1, y1, x2, y2, paint);
        }
    }
}
