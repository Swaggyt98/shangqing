package com.zosoftware.solid.ui.home.care;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.util.Util;
import com.zosoftware.solid.R;
import com.zosoftware.solid.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingDeque;

public class ECGChart extends View {
    private float mGraphMax = 356.f;  //心电波动区间256.f
    private int mRedrawInterval = 1; //屏幕刷新间隔 ms
    private int mRedrawPoints;

    public static final int SWEEP_MODE = 0;
    public static final int FLOW_MODE = 1;
    private int mLineColor;
    private int mGridColor;
    private int mArrowColor;

    private int mWindowSize;
    private int mWindowCount = 2;
    private int ONEWINDOW = 240;//240
    private LinkedBlockingDeque<Integer> mInputBuf; //波形数据纵坐标
    private Vector<Integer> mDrawingBuf;            //下标标识横坐标 数值标识纵坐标

    private Paint mPaint;

    private Paint mPaintGrid;
    private Paint mPaintRuler;
    private Paint mPaintArrow;
    private Paint mPaintSmallGrid;

    private Paint mMaskBarPaint;
    private int mDrawPosition;

    private Activity mActivity;

    private int mGraphMode = 1;
    private boolean mGrid = true;
    private boolean mArrow = false;
    private boolean mFullscreen = false;
    private boolean isConnected = false;
    TimerTask mDrawEmitter;
    Timer mTimer;

    //画网格线第二种方法
    private Paint paint;
    int horizontalBigGirdNum = 4;// 横向的线，即纵向大格子的数量，每个大格子里面包含5个小格子
    int verticalBigGirdNum = 2;
    private int width;
    private int height;
    private int widthOfSmallGird;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        widthOfSmallGird = width / (verticalBigGirdNum * 5); // 小网格的宽度，每个大网格有 5 个小网格
    }

    public ECGChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity) context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ECGChart,
                0, 0);

        try {
            //线颜色
            mLineColor = a.getColor(R.styleable.ECGChart_lineColor, Color.RED);
            //网格颜色
            mGridColor = a.getColor(R.styleable.ECGChart_gridColor, Color.argb(0x33, 0x00, 0xFF, 0x00));
            //箭头颜色
            mArrowColor = a.getColor(R.styleable.ECGChart_arrowColor, Color.rgb(255, 255, 255));

            mGraphMode = a.getInt(R.styleable.ECGChart_graphMode, SWEEP_MODE);
            mGrid = a.getBoolean(R.styleable.ECGChart_grid, true);
            mArrow = a.getBoolean(R.styleable.ECGChart_arrow, false);

            //窗口大小
            mWindowSize = a.getInt(R.styleable.ECGChart_windowSize, ONEWINDOW * mWindowCount);

            //数据源
            mInputBuf = new LinkedBlockingDeque<>();
            mDrawingBuf = new Vector<>();

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(1f);
            mPaint.setColor(mLineColor);

            //画大格
            mPaintGrid = new Paint();
            mPaintGrid.setColor(mGridColor);
            mPaintGrid.setStrokeWidth(2f);
            //画小格
            mPaintSmallGrid = new Paint();
            mPaintSmallGrid.setColor(mGridColor);
            mPaintSmallGrid.setStrokeWidth(1f);

            //画箭头
            mPaintArrow = new Paint();
            mPaintArrow.setColor(mArrowColor);
            mPaintArrow.setStrokeWidth(1);
            mPaintArrow.setStyle(Paint.Style.STROKE);

            //尺寸
            mPaintRuler = new Paint();
            mPaintRuler.setColor(Color.argb(0xAA, 0xFF, 0xFF, 0xFF));


            mMaskBarPaint = new Paint();
            mMaskBarPaint.setColor(Color.rgb(0x33, 0x33, 0x33));
            mMaskBarPaint.setStyle(Paint.Style.STROKE);

            // TODO: 2017/10/25 0025
            mGraphMode = FLOW_MODE;
        } finally {
            a.recycle();
        }
        init();
        //画网格线
        paint = new Paint();                            // 画网格的 Paint
        paint.setStyle(Paint.Style.STROKE);
    }

    private void init() {
        mRedrawPoints = ONEWINDOW * mRedrawInterval / 100;
        for (int i = 0; i < mWindowSize; i++)
            mDrawingBuf.add(128);

        mDrawEmitter = new TimerTask() {
            @Override
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        //L.d("inputBufSizeinput", mInputBuf.size() + "");
                        //L.d("inputBufSizedraw", mDrawingBuf.size() + "");
                    }
                });
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mDrawEmitter, 0, mRedrawInterval);
    }

    private void refreshData() {
        if (!isConnected) {
            //L.d(" isConnected  return");
            return;
        }
        if (mInputBuf.size() < mRedrawPoints) {
            return;
        }
        if (mGraphMode == SWEEP_MODE) {
            for (int i = 0; i < mRedrawPoints; i++) {
                int val = mInputBuf.pollFirst();
                mDrawingBuf.remove(mDrawPosition);
                mDrawingBuf.add(mDrawPosition++, val);
                if (mDrawPosition >= mWindowSize)
                    mDrawPosition = 0;
            }
        } else {
            for (int i = 0; i < mRedrawPoints; i++) {
                int val = mInputBuf.pollFirst();
                mDrawingBuf.remove(0);
                mDrawingBuf.add(val);
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = this.getWidth();           //控件宽度
        float height = this.getHeight();         //控件高度

        //L.e("ECGCHAR width = "+width + "  height ="+height);
        /*if (mDrawingBuf.size() < mWindowSize) // return?
            return;*/

        float mapRatio = width / mWindowSize;   //
        int start = mDrawingBuf.get(0);
        if (mGraphMode == FLOW_MODE) {
            //L.e("FLOW_MODE");
            for (int i = 1; i < mWindowSize; i++) {
                //画波形
                if (i >= mDrawingBuf.size())
                    break;
                int end = mDrawingBuf.get(i);
                //start / mGraphMax 当前点占控件相对位置
                /*canvas.drawLine(i , start / mGraphMax * height,
                        i+1 ,end / mGraphMax * height, mPaint);*/
                canvas.drawLine(i * mapRatio, (1 - start / mGraphMax) * height,
                        (i + 1) * mapRatio, (1 - end / mGraphMax) * height, mPaint);
                /*drawAnimLine(canvas, i * mapRatio, (1 - start / mGraphMax) * height,
                        (i + 1) * mapRatio, (1 - end / mGraphMax) * height);*/
                start = end;
            }
        }

        if (mGraphMode == SWEEP_MODE) {
            Utils.loginfo("SWEEP_MODE");
            canvas.drawRect((mDrawPosition - 10) * mapRatio,
                    0,
                    (mDrawPosition + 10) * mapRatio,
                    height,
                    mMaskBarPaint);
        }

        if (mGrid) {
            //画背景格
            drawGridLine(canvas);
        }
    }

    /*private ValueAnimator valueAnimator;
    private float tempX;
    private float tempY;

    private void drawAnimLine(final Canvas canvas, final float startX, final float startY, final float endX, final float endY) {
        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                //canvas.drawLine(startX + DoubleUtil.mul());
                tempX = FloatUtil.add(startX, FloatUtil.mul(fraction, FloatUtil.sub(endX, startX)));
                tempY = FloatUtil.add(startY, FloatUtil.mul(fraction, FloatUtil.sub(endY, startY)));
                canvas.drawLine(startX, startY, tempX, tempY, mPaint);
            }
        });
        valueAnimator.start();
    }*/

    public void setMode(int type) {
        mInputBuf.clear();
        mGraphMode = type;
    }

    public void setConnection(boolean isConnect) {
        if (!isConnect)
            mInputBuf.clear();
        isConnected = isConnect;
    }

    /**
     * 画网格 第一种方式
     */
    private void drawGird(Canvas canvas) {
        paint.setColor(Color.GREEN);                   // 网格浅绿
        // 横向的网格
        for (int i = 0; i <= verticalBigGirdNum * 5; i++) {
            if (i % 5 == 0) {       // 每隔 5 个小格，线变粗
                paint.setStrokeWidth(3);
            } else {
                paint.setStrokeWidth(1);
            }
            canvas.drawLine(i * widthOfSmallGird, 0, i * widthOfSmallGird, height, paint);     // 画线
        }

        //  纵向的网格
        for (int i = 0; i <= horizontalBigGirdNum * 5; i++) {
            if (i % 5 == 0) {    // 每隔 5 个小格，线变粗
                paint.setStrokeWidth(3);
            } else {
                paint.setStrokeWidth(1);
            }
            canvas.drawLine(0, i * widthOfSmallGird, width, i * widthOfSmallGird, paint);
        }
    }

    /**
     * 画背景线 第二种方式
     *
     * @param canvas
     */
    public void drawGridLine(Canvas canvas) {
        float width = this.getWidth();
        float height = this.getHeight();
        int gridXNumber = 8;
        int gridYNumber = 4; //最大值4,0mV
        for (int i = 0; i < gridXNumber; i++) {
            canvas.drawLine(i * width / gridXNumber, 0,
                    i * width / gridXNumber, height, mPaintGrid);
        }

        for (int i = 0; i < gridXNumber; i++) {
            for (int j = 0; j < 10; j++)
                canvas.drawLine(i * width / gridXNumber + j * width / gridXNumber / 10f, 0,
                        i * width / gridXNumber + j * width / gridXNumber / 10f, height, mPaintSmallGrid);
        }

        for (int i = 0; i < gridYNumber; i++) {
            canvas.drawLine(0, i * height / gridYNumber,
                    width, i * height / gridYNumber, mPaintGrid);
        }

        for (int i = 0; i < gridYNumber; i++) {
            for (int j = 0; j < 10; j++)
                canvas.drawLine(0, i * height / gridYNumber + j * height / gridYNumber / 10f,
                        width, i * height / gridYNumber + j * height / gridYNumber / 10f, mPaintSmallGrid);
        }

        //画刻度
        float pX = 10;
        float pY = height - 10;
        float dx = width / gridXNumber;
        float dy = height / gridYNumber;
        canvas.drawLine(pX, pY, pX, pY - dy + 5, mPaintArrow);
        canvas.drawLine(pX, pY, pX + dx - 5, pY, mPaintArrow);

        Path path = new Path();
        path.moveTo(0, -10);
        path.lineTo(5, 0);
        path.lineTo(-5, 0);
        path.close();

        Path path2 = new Path();
        path2.moveTo(10, 0);
        path2.lineTo(0, 5);
        path2.lineTo(0, -5);
        path2.close();

        path.offset(pX, pY - dy + 5);
        path2.offset(pX + dx - 5, pY);
        canvas.drawPath(path, mPaintArrow);
        canvas.drawPath(path2, mPaintArrow);
        mPaintArrow.setStrokeWidth(1);
        mPaintArrow.setAntiAlias(true);
        if (mArrow) {//写文字
            canvas.drawText("250 ms", pX + 28, pY - 10, mPaintArrow);
            canvas.drawText("1.0 mV", pX + 8, pY - dy + 15, mPaintArrow);
        }
    }

    public void addEcgData(final int data) {
        if (data != 0) {
            mInputBuf.addLast(data);
        }
        //L.i("size", "" + mInputBuf.size()+" data "+data);
    }
    private void checkBufOverflow() {
        Utils.loginfo("size "+ mInputBuf.size() + "");
        if (mInputBuf.size() > 2000)
            mInputBuf.clear();
    }

    /**
     * 定时器关闭-释放资源
     */
    public void clearTimeData() {
        mDrawEmitter.cancel();
        mTimer.cancel();
        mTimer.purge();
    }
}