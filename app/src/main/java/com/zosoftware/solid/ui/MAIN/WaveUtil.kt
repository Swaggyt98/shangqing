package com.zosoftware.solid.ui.MAIN

import com.bumptech.glide.util.Util
import com.zosoftware.solid.utils.Utils
import java.util.Timer
import java.util.TimerTask


class WaveUtil {

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    lateinit var datas: FloatArray

    fun  changeDatas(i:Float, waveShowView:WaveView) {
        datas = FloatArray(1)
        datas[0] = i/1000;

        waveShowView.showLines(datas)
    }


//    fun showWaveDatas(waveShowView: WaveView) {
//        datas = FloatArray(1)
//        timer = Timer()
//        timerTask = object : TimerTask() {
//            override fun run() {
//                /*                if (datas[0] > 25f){
//                                    datas[0] = -25f
//                                }*/
//                waveShowView.showLines(datas)
//            }
//        }
//        //500表示调用schedule方法后等待500ms后调用run方法，50表示以后调用run方法的时间间隔
//        timer!!.schedule(timerTask, 500, 10)
//    }

    fun stop() {
        if (timer != null) {
            timer!!.cancel()
            timer!!.purge()
            timer = null
        }
        if (null != timerTask) {
            timerTask!!.cancel()
            timerTask = null
        }
    }
}