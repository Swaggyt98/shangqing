package com.zosoftware.solid.ui.MAIN

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class WaveView : View {
    private val NAMESPACE = "http://schemas.android.com/apk/res-auto"

    //绘制模式
    private var drawMode = 0

    //宽高
    private var mWidth = 0f
    private var mHeight = 0f

    //网格画笔
    private var mLinePaint: Paint? = null

    //数据线画笔
    private var mWavePaint: Paint? = null

    //线条的路径
    private var mPath: Path? = null

    //保存已绘制的数据坐标
    lateinit var dataArray: FloatArray

    //数据最大值和最小值，默认-20~20之间
    private var MAX_VALUE = 20f
    private var MIN_VALUE = -20f

    //线条粗细
    private var WAVE_LINE_STROKE_WIDTH = 3f

    //波形颜色
    private var waveLineColor = Color.parseColor("#EE4000")

    //当前的x，y坐标
    private var nowX = 0f
    private var nowY = 0f
    private var startY = 0f

    //线条的长度，可用于控制横坐标，水平投影上的长度
    private var WAVE_LINE_WIDTH = 10

    //数据点的数量
    private var row = 0
    private var draw_index = 0
    private var isRefresh = false

    //常规模式下，需要一次绘制的点的数量
    private var draw_point_length = 0

    //网格是否可见
    private var gridVisible = false

    //网格的宽高
    private val GRID_WIDTH = 50

    //网格的横线和竖线的数量
    private var gridHorizontalNum = 0
    private var gridVerticalNum = 0

    //网格线条的粗细
    private val GRID_LINE_WIDTH = 2

    //网格颜色
    private var gridLineColor = Color.parseColor("#1b4200")

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mWidth = w.toFloat()
        mHeight = h.toFloat()
        super.onSizeChanged(w, h, oldw, oldh)
    }

    private fun init(attrs: AttributeSet?) {
        MAX_VALUE = attrs!!.getAttributeIntValue(NAMESPACE, "max_value", 20).toFloat()
        MIN_VALUE = -MAX_VALUE
        WAVE_LINE_WIDTH = attrs.getAttributeIntValue(NAMESPACE, "wave_line_width", 10)
        WAVE_LINE_STROKE_WIDTH = attrs.getAttributeIntValue(NAMESPACE, "wave_line_stroke_width", 3).toFloat()
        gridVisible = attrs.getAttributeBooleanValue(NAMESPACE, "grid_visible", true)
        drawMode = attrs.getAttributeIntValue(NAMESPACE, "draw_mode", NORMAL_MODE)
        val wave_line_color = attrs.getAttributeValue(NAMESPACE, "wave_line_color")
        if (wave_line_color != null && wave_line_color.isNotEmpty()) {
            waveLineColor = Color.parseColor(wave_line_color)
        }
        val grid_line_color = attrs.getAttributeValue(NAMESPACE, "grid_line_color")
        if (grid_line_color != null && grid_line_color.isNotEmpty()) {
            gridLineColor = Color.parseColor(grid_line_color)
        }
        val wave_background = attrs.getAttributeValue(NAMESPACE, "wave_background")
        if (wave_background != null && wave_background.isNotEmpty()) {
            setBackgroundColor(Color.parseColor(wave_background))
        }
        mLinePaint = Paint()
        mLinePaint!!.style = Paint.Style.STROKE
        mLinePaint!!.strokeWidth = GRID_LINE_WIDTH.toFloat()
        mLinePaint!!.isAntiAlias = true
        mWavePaint = Paint()
        mWavePaint!!.style = Paint.Style.STROKE
        mWavePaint!!.color = waveLineColor
        mWavePaint!!.strokeWidth = WAVE_LINE_STROKE_WIDTH
        mWavePaint!!.isAntiAlias = true
        mPath = Path()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mWidth = measuredWidth.toFloat()
        mHeight = measuredHeight.toFloat()
        gridHorizontalNum = (mHeight / GRID_WIDTH).toInt()
        gridVerticalNum = (mWidth / GRID_WIDTH).toInt()
        row = (mWidth / WAVE_LINE_WIDTH).toInt()
        dataArray = FloatArray(row)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (gridVisible) {
            drawGrid(canvas)
        }
        when (drawMode) {
            0 -> drawWaveLineNormal(canvas)
            1 -> drawWaveLineLoop(canvas)
        }
        draw_index += 1
        if (draw_index >= row) {
            draw_index = 0
        }
    }

    private fun drawWaveLineNormal(canvas: Canvas) {
//        justifyData()
        drawPathFromDatas(canvas, 0, row - 1)
        for (i in 0 until row - draw_point_length) {
            dataArray[i] = dataArray[i + draw_point_length]
        }
    }

    private fun justifyData() {
        val max = dataArray.maxOrNull() ?: 0f
        val min = dataArray.minOrNull() ?: 0f
        MAX_VALUE = max + 30
        MIN_VALUE = min - 30
    }

    private fun drawWaveLineLoop(canvas: Canvas) {
        drawPathFromDatas(canvas, if (row - 1 - draw_index > 8) 0 else 8 - (row - 1 - draw_index), draw_index)
        drawPathFromDatas(canvas, (draw_index + 8).coerceAtMost(row - 1), row - 1)
    }

    private fun drawPathFromDatas(canvas: Canvas, start: Int, end: Int) {
        mPath!!.reset()
        startY = mHeight / 2 - dataArray[start] * (mHeight / (MAX_VALUE - MIN_VALUE))
        mPath!!.moveTo((start * WAVE_LINE_WIDTH).toFloat(), startY)
        for (i in start + 1 until end + 1) {
            if (isRefresh) {
                isRefresh = false
                return
            }
            nowX = (i * WAVE_LINE_WIDTH).toFloat()
            var dataValue = dataArray[i]
            if (dataValue > MAX_VALUE) {
                dataValue = MAX_VALUE
            } else if (dataValue < MIN_VALUE) {
                dataValue = MIN_VALUE
            }
            nowY = mHeight / 2 - dataValue * (mHeight / (MAX_VALUE - MIN_VALUE))
            mPath!!.lineTo(nowX, nowY)
        }
        canvas.drawPath(mPath!!, mWavePaint!!)
    }

    private fun drawGrid(canvas: Canvas) {
        mLinePaint!!.color = gridLineColor
        for (i in 0 until gridHorizontalNum + 1) {
            canvas.drawLine(0f, (i * GRID_WIDTH).toFloat(), mWidth, (i * GRID_WIDTH).toFloat(), mLinePaint!!)
        }
        for (i in 0 until gridVerticalNum + 1) {
            canvas.drawLine((i * GRID_WIDTH).toFloat(), 0f, (i * GRID_WIDTH).toFloat(), mHeight, mLinePaint!!)
        }
    }

    fun showLines(lines: FloatArray) {
        when (drawMode) {
            0 -> {
                draw_point_length = lines.size
                showLinesNormal(lines)
            }
            1 -> showLinesLoop(lines)
        }
        postInvalidate()
    }

    private fun showLinesNormal(lines: FloatArray) {
        for (i in lines.indices) {
            dataArray[row - (lines.size - i)] = lines[i]
        }
    }

    private fun showLinesLoop(lines: FloatArray) {
        var temporary_index = draw_index
        for (i in lines.indices) {
            dataArray[temporary_index] = lines[i]
            temporary_index += 1
            if (temporary_index > dataArray.size - 1) temporary_index = 0
        }
        draw_index = if (temporary_index - 1 < 0) { row - 1 } else { temporary_index - 1 }
    }

    companion object {
        var NORMAL_MODE = 0
    }
}
