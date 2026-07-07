package com.zosoftware.solid.ui.MAIN

import com.zosoftware.solid.R
import android.annotation.SuppressLint
import android.os.*
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.zosoftware.solid.databinding.ActivityMain2Binding
import java.util.*
import kotlin.collections.ArrayDeque

class MainActivity : FragmentActivity() {
    private var txt_serial_nbp: TextView? = null
    private var txt_serial_spo2: TextView? = null
    private var txt_serial_hr: TextView? = null
    private var txt_serial_rr: TextView? = null
    private var txt_serial_temp: TextView? = null

    companion object {
        const val READ_WAIT_MILLIS = 2000
        const val HANDLER_RECEIVE_BUNDLE = "serialReceive"
        const val HANDLER_RECEIVE_MESSAGE_WHAT = 124
        const val DEFAULT_RESP_RATE = 17f // 默认呼吸频率
        const val DEFAULT_RESP_RATE_INTERVAL = 20000 // 默认呼吸频率的更新间隔（毫秒）
    }

    private lateinit var binding: ActivityMain2Binding
    private lateinit var serialDriver: UsbSerialDriver
    private lateinit var serialPort: UsbSerialPort
    private var availableDrivers = mutableListOf<UsbSerialDriver>()
    private var serialConnected = false
    private var encodingFormat = "GBK"
    private var waveUtil: WaveUtil? = null
    private var respRateTimer: Timer? = null
    private val ecgDataQueue = ArrayDeque<Float>()
    private val windowSize = 5 * 20  // 5秒的窗口，假设每秒20次更新

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化控件
        txt_serial_nbp = findViewById<Button>(R.id.txt_serial_nbp)
        txt_serial_spo2 = findViewById<Button>(R.id.txt_serial_spo2)
        txt_serial_hr = findViewById<Button>(R.id.txt_serial_hr)
        txt_serial_rr = findViewById<Button>(R.id.txt_serial_rr)
        txt_serial_temp = findViewById<Button>(R.id.txt_serial_temp)

        // 初始化波形绘制工具
        waveUtil = WaveUtil()
//        waveUtil!!.showWaveDatas(binding.waveView1)

        // 初始化定时器
        respRateTimer = Timer()

        // 开始接收串口数据
        // funSerialConnect()
        // thread { funSerialReceive() }
    }

    // 处理接收到的数据
    private val handler = object : Handler(Looper.getMainLooper()) {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                HANDLER_RECEIVE_MESSAGE_WHAT -> {
                    val string = msg.data.getString(HANDLER_RECEIVE_BUNDLE)
                    if (string!!.startsWith("x:")) {
                        val values = string.substring(2)
                        if (values.length >= 22) {
                            try {
                                val ecgData = values.substring(0, 3).toFloat()
//                                waveUtil?.changeDatas(ecgData)

                                // 存储心电图数据以计算呼吸频率
                                storeEcgData(ecgData)

                                // 计算呼吸频率
                                val respRate = calculateRespRate()
                                binding.txtSerialRr.text = respRate.toString()

                                // 更新其他数据
                                val sbp = values.substring(6, 9)
                                val dbp = values.substring(9, 12)
                                binding.txtSerialNbp.text = "$sbp/$dbp"

                                val hr = values.substring(12, 15)
                                binding.txtSerialHr.text = hr

                                val spo2 = values.substring(15, 18)
                                binding.txtSerialSpo2.text = spo2

                                val tempWhole = values.substring(18, 22)
                                val temp = "${tempWhole.substring(0, 2)}.${tempWhole.substring(2, 4)}"
                                binding.txtSerialTemp.text = temp

                                // 重置定时器
                                resetRespRateTimer()

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }

    // 重置呼吸频率计算定时器
    private fun resetRespRateTimer() {
        respRateTimer?.cancel()
        respRateTimer = Timer()
        respRateTimer?.schedule(object : TimerTask() {
            override fun run() {
                // 定时器到期时，更新呼吸频率为默认值 17
                runOnUiThread {
                    binding.txtSerialRr.text = DEFAULT_RESP_RATE.toString()
                }
            }
        }, DEFAULT_RESP_RATE_INTERVAL.toLong())
    }

    // 存储心电图数据
    private fun storeEcgData(ecgData: Float) {
        if (ecgDataQueue.size >= windowSize) {
            ecgDataQueue.removeFirst()
        }
        ecgDataQueue.addLast(ecgData)
    }

    // 计算呼吸频率
    private fun calculateRespRate(): Float {
        if (ecgDataQueue.size < windowSize) {
            return DEFAULT_RESP_RATE // 当数据不足时返回默认值
        }

        val filteredData = ecgDataQueue.toList().let { data ->
            data.mapIndexed { index, value ->
                if (index == 0) value
                else 0.1f * value + 0.9f * data[index - 1]
            }
        }

        val peaks = filteredData.indices.filter {
            it > 0 && it < filteredData.size - 1 && filteredData[it] > filteredData[it - 1] && filteredData[it] > filteredData[it + 1]
        }

        val peakIntervals = peaks.zipWithNext { a, b -> (b - a) / 20f }

        return peakIntervals.average().toFloat().let {
            if (it.isNaN()) DEFAULT_RESP_RATE else 60f / it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        respRateTimer?.cancel()
        waveUtil?.stop()
    }
}
