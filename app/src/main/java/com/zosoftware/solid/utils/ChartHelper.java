package com.zosoftware.solid.utils;

import android.graphics.Color;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * create by hj on 2020/4/25
 **/
public class ChartHelper {

    //public static float getRandom(Float seed) {
  //      DecimalFormat mDecimalFormat = new DecimalFormat("#.00");
    //    Random mRandom = new Random();
    //    return Float.valueOf(mDecimalFormat.format(mRandom.nextFloat() * seed));
   // }

    private static int maxCount = 1000; //集合最大存储数量

    public static void addEntry(List<Entry> mData, LineChart lineChart, float yValues) {     //用于向折线图中添加新的数据点，并更新图表显示。
        if (lineChart != null
                && lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            int size = mData.size();
            if (size == 0) {
                Entry entry = new Entry(maxCount, yValues);
                mData.add(entry);
            } else {
                boolean needRemove = false;
                for (Entry e : mData) {
                    float x = e.getX() - 1;
                    if (x < 0) {
                        needRemove = true;
                    }
                    e.setX(x);
                }
                if (needRemove) {
                    mData.remove(0);
                }
                Entry entry = new Entry(maxCount, yValues);
                mData.add(entry);
            }
            LineData lineData = new LineData(getSet(mData));
            lineData.setDrawValues(false);
            lineChart.setData(lineData);
            lineChart.invalidate();
        }
    }

    public static void initChart(List<Entry> mData, LineChart lineChart, long maxYValue) {  //用于初始化折线图，包括设置轴线、网格线、颜色等基本样式，以及数据集的初始状态。
        int lineColor = Color.parseColor("#ebebeb");
        int textColor = Color.parseColor("#999999");

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setEnabled(false);


        YAxis axisLeft = lineChart.getAxisLeft();
        axisLeft.setAxisMinimum(-15000);
        axisLeft.setLabelCount(10);
        axisLeft.setAxisMaximum(maxYValue);
        axisLeft.setGridColor(lineColor);
        axisLeft.setTextColor(textColor);
        axisLeft.setAxisLineColor(lineColor);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(maxCount);
        xAxis.setLabelCount(maxCount);
        Collections.sort(mData, new EntryXComparator());
        LineData lineData = new LineData(getSet(mData));
        lineData.setDrawValues(false);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    public static void deinitChart(LineChart lineChart) {
        // 清除数据
        lineChart.clear();

        // 重置所有设置
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(true);
        lineChart.getDescription().setEnabled(true);
        lineChart.getLegend().setEnabled(true);
        lineChart.getAxisRight().setEnabled(true);
        lineChart.getXAxis().setEnabled(true);

        // 重置Y轴
        YAxis axisLeft = lineChart.getAxisLeft();
        axisLeft.resetAxisMinimum();
        axisLeft.resetAxisMaximum();
        axisLeft.setLabelCount(6, true);
        axisLeft.setGridColor(Color.LTGRAY);
        axisLeft.setTextColor(Color.BLACK);
        axisLeft.setAxisLineColor(Color.LTGRAY);

        // 重置X轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.resetAxisMinimum();
        xAxis.resetAxisMaximum();
        xAxis.setLabelCount(5, true);

        // 重置图表
        lineChart.invalidate();
    }
    public static void showEntry(List<Entry> mData, LineChart lineChart) {
        LineData lineData = new LineData(getSet(mData));
        lineData.setDrawValues(false);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
    private static LineDataSet getSet(List<Entry> mData) {  //图表绘制数据的样式设置，比如线条的颜色、是否绘制圆点、是否填充颜色等。
        int valueColor = Color.parseColor("#2979FF");
        LineDataSet set = new LineDataSet(mData, "");
        set.setDrawFilled(false);
        set.setFillColor(valueColor);
        set.setColor(valueColor);
        set.setValueTextColor(valueColor);
        set.setDrawCircles(false);
      //  set.setLineWidth(2f); // 这里改线宽度
        set.setMode(LineDataSet.Mode.LINEAR);
        return set;
    }
}
