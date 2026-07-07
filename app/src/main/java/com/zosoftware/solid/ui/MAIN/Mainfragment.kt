//package com.zosoftware.solid.ui.MAIN
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import com.zosoftware.solid.R
//
//var NPB = ""//无创血压
//var SpO2= ""//血氧
//var HR= "" //心率
//var RR= ""//呼吸率
//var Temp=""//体温
//
//class Mainfragment : Fragment() {
//
//    private var txt_serial_nbp: TextView? = null
//    private var txt_serial_spo2: TextView? = null
//    private var txt_serial_hr: TextView? = null
//    private var txt_serial_rr: TextView? = null
//    private var txt_serial_temp: TextView? = null
//    private var txt_Serial_Info: TextView? = null
//    private var txt_Serial1: TextView? = null
//    private var txt_Serial2: TextView? = null
//    private var wave_View1: WaveView? = null
//    private var wave_View2: WaveView? = null
//    private var waveUtil1: WaveUtil? = null
//    private var waveUtil2: WaveUtil? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.activity_main2, container, false)
//    }
//
//    override fun onStart() {
//        super.onStart()
//        //寻找控件
//        txt_serial_nbp = requireView().findViewById(R.id.txt_serial_nbp)
//        txt_serial_spo2 = requireView().findViewById(R.id.txt_serial_spo2)
//        txt_serial_hr = requireView().findViewById(R.id.txt_serial_hr)
//        txt_serial_rr = requireView().findViewById(R.id.txt_serial_rr)
//        txt_serial_temp = requireView().findViewById(R.id.txt_serial_temp)
//        txt_Serial_Info = requireView().findViewById(R.id.txt_serial_info)
//        txt_Serial1 = requireView().findViewById(R.id.txt_serial1)
//        wave_View1= requireView().findViewById(R.id.wave_view1)
//        txt_Serial2 = requireView().findViewById(R.id.txt_serial2)
//        wave_View2= requireView().findViewById(R.id.wave_view2)
//
//        NPB = txt_serial_nbp!!.text.toString()
//        SpO2 = txt_serial_spo2!!.text.toString()
//        HR  = txt_serial_hr!!.text.toString()
//        RR  = txt_serial_rr!!.text.toString()
//        Temp = txt_serial_temp!!.text.toString()
//
//        //波形绘制工具
//        waveUtil1 = WaveUtil()
//        waveUtil1!!.showWaveDatas(wave_View1!!)
//        waveUtil2 = WaveUtil()
//        waveUtil2!!.showWaveDatas(wave_View2!!)
//    }
//
//    /**
//     * 将蓝牙接收到的数据显示出来
//     */
//    @SuppressLint("SetTextI18n")
//    fun onReceiveData(data: String) {
//        val stringBuffer1 = StringBuffer()
//        val stringBuffer2 = StringBuffer()
//        if (data.startsWith("x:")){
//            if (data.length==8){
//                val string1 = data.substring(2,5)
//                val string2 = data.substring(5,8)
//                try {
//                    waveUtil1?.changeDatas(string1.toFloat())
//                    waveUtil2?.changeDatas(string2.toFloat())
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                stringBuffer1.append(string1)
//                txt_Serial1?.text = stringBuffer1.toString()
//                stringBuffer2.append(string2)
//                txt_Serial2?.text = stringBuffer2.toString()
//            }else {
//                if (data.length >= 24) {
//                    val string1 = data.substring(2, 5)
//                    val string2 = data.substring(5, 8)
//                    try {
//                        waveUtil1?.changeDatas(string1.toFloat())
//                        waveUtil2?.changeDatas(string2.toFloat())
//                        txt_serial_nbp?.text = "${data.substring(8, 11)}/${data.substring(11, 14)}"
//                        txt_serial_hr?.text = data.substring(14, 17)
//                        txt_serial_spo2?.text = data.substring(17, 20)
//                        txt_serial_temp?.text = "${data.substring(20, 22)}.${data.substring(22, 24)}"
//                        NPB = txt_serial_nbp!!.text.toString()
//                        SpO2 = txt_serial_spo2!!.text.toString()
//                        HR  = txt_serial_hr!!.text.toString()
//                        RR  = txt_serial_rr!!.text.toString()
//                        Temp = txt_serial_temp!!.text.toString()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                    stringBuffer1.append(string1)
//                    txt_Serial1?.text = stringBuffer1.toString()
//                    stringBuffer2.append(string2)
//                    txt_Serial2?.text = stringBuffer2.toString()
//                } else {
//                    //以防数据缺失
//                    try {
//                        val string1 = data.substring(2, 5)
//                        val string2 = data.substring(5, 8)
//                        waveUtil1?.changeDatas(string1.toFloat())
//                        waveUtil2?.changeDatas(string2.toFloat())
//                        txt_serial_nbp?.text = "${data.substring(8, 11)}/${data.substring(11, 14)}"
//                        stringBuffer1.append(string1)
//                        txt_Serial1?.text = stringBuffer1.toString()
//                        stringBuffer2.append(string2)
//                        txt_Serial2?.text = stringBuffer2.toString()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//        }
//    }
//
//    //波形绘制工具销毁
//    override fun onStop() {
//        super.onStop()
//        waveUtil1?.stop()
//        waveUtil2?.stop()
//    }
//}
