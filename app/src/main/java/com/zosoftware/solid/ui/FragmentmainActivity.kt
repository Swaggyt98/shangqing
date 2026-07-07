//package com.zosoftware.solid.ui
//
//import android.annotation.SuppressLint
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothDevice
//import android.bluetooth.BluetoothSocket
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import android.view.View
//import android.view.Window
//import android.widget.Button
//import android.widget.LinearLayout
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.FragmentActivity
//import androidx.fragment.app.FragmentTransaction
//import com.zosoftware.solid.R
//import com.zosoftware.solid.ui.BT.BTActivity
//import com.zosoftware.solid.ui.BT.EXTRA_DEVICE_ADDRESS
//import com.zosoftware.solid.ui.GPT.Chatfragment
//import com.zosoftware.solid.ui.MAIN.Mainfragment
//import java.io.IOException
//import java.io.InputStream
//import java.util.UUID
//
//
//class FragmentmainActivity: FragmentActivity(),View.OnClickListener{
//    private val MY_UUID = "00001101-0000-1000-8000-00805F9B34FB" //SPP服务UUID号
//    private val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter() //获取蓝牙实例
//    var mBluetoothDevice: BluetoothDevice? = null //蓝牙设备
//    var mBluetoothSocket: BluetoothSocket? = null //蓝牙通信Socket
//    var bRun = true //运行状态
//    var bThread = false //读取线程状态
//    private var `is` //输入流，用来接收蓝牙数据
//            : InputStream? = null
//    private var smsg = "" //显示用数据缓存
//
//    //声明三个Tab的布局文件
//    private var mTab1: LinearLayout? = null
//    private var mTab2: LinearLayout? = null
//    private var mTab3: LinearLayout? = null
//    //声明三个Tab的Button
//    private var mImg1: Button? = null
//    private var mImg2: Button? = null
//    private var connect:Button? = null
//    //声明两个Tab分别对应的Fragment
//    private var mFrag1: Mainfragment? = null
//    private var mFrag2: Chatfragment? = null
//    //var tiaoshi:TextView?= null
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE) // 隐藏标题栏
//        setContentView(R.layout.fragmentmain)
//        initViews() //初始化控件
//        initEvents() //初始化事件
//        selectTab(0)//默认选中第一个Tab
//
//
//
///*         *//*TODO 发送测试*//*
//        var number = 10
//        handler.postDelayed(object: Runnable{
//            override fun run() {
//                ++number
//                runOnUiThread { mFrag1?.onReceiveData("a:${number},${number},${number},${number},${number},${number}") }
//                handler.postDelayed(this, 1000)
//            }
//        }, 1000)*/
//
//        //如果打不开蓝牙提示信息，结束程序
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(
//                applicationContext,
//                "无法打开手机蓝牙，请确认手机是否有蓝牙功能！",
//                Toast.LENGTH_SHORT
//            ).show()
//            finish()
//            return
//        }
//        //连接按钮响应
//        val connect = findViewById<Button>(R.id.connect)
//        val txt_serial_info = findViewById<TextView>(R.id.txt_serial_info)
//        connect.setOnClickListener(View.OnClickListener {
//            if (mBluetoothAdapter.isEnabled == false) {
//                Toast.makeText(applicationContext, " 请先打开蓝牙", Toast.LENGTH_LONG).show()
//                return@OnClickListener
//            }
//            //如果未连接设备则打开BTActivity搜索设备
//            if (mBluetoothSocket == null) {
//                val serveIntent = Intent(applicationContext, BTActivity::class.java) //跳转活动
//                startActivityForResult(serveIntent, 1) //设置返回宏定义
//            } else {
//                //关闭连接socket
//                try {
//                    bRun = false
//                    Thread.sleep(2000)
//                    `is`!!.close()
//                    mBluetoothSocket!!.close()
//                    mBluetoothSocket = null
//                    connect!!.text = "蓝牙连接"
//                    txt_serial_info!!.text="设备信息"
//
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//        })
//        // 设置设备可以被搜索
//        object : Thread() {
//            @SuppressLint("MissingPermission")
//            override fun run() {
//                if (mBluetoothAdapter.isEnabled == false) {
//                    mBluetoothAdapter.enable()
//                }
//            }
//        }.start()
//    }
//
//    @SuppressLint("MissingPermission", "SetTextI18n")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        val txt_serial_info = findViewById<TextView>(R.id.txt_serial_info)
//        when (requestCode) {
//            1 ->                 // 响应返回结果
//                if (resultCode == RESULT_OK) {   //连接成功，由BTActivity设置返回
//                    // MAC地址，由BTActivity设置返回
//                    val address = data?.extras!!.getString(EXTRA_DEVICE_ADDRESS)
//                    // 得到蓝牙设备句柄
//                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address)
//
//                    // 用服务号得到socket
//                    try {
//                        mBluetoothSocket = mBluetoothDevice!!.createRfcommSocketToServiceRecord(
//                            UUID.fromString(MY_UUID)
//                        )
//                    } catch (e: IOException) {
//                        Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show()
//                    }
//                    //连接socket
//                    val connect = findViewById<Button>(R.id.connect)
//                    try {
//                        mBluetoothSocket!!.connect()
//                        Toast.makeText(
//                            this,
//                            "连接" + mBluetoothDevice!!.name + "成功！",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        txt_serial_info.text="设备:"+mBluetoothDevice!!.name
//                        connect.text = "蓝牙断开"
//                    } catch (e: IOException) {
//                        try {
//                            Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show()
//                            mBluetoothSocket!!.close()
//                            mBluetoothSocket = null
//                        } catch (ee: IOException) {
//                            Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show()
//                        }
//                        return
//                    }
//
//                    //打开接收线程
//                    `is` = try {
//                        mBluetoothSocket!!.inputStream //得到蓝牙数据输入流
//                    } catch (e: IOException) {
//                        Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT).show()
//                        return
//                    }
//                    if (bThread == false) {
//                        readThread.start()
//                        bThread = true
//                    } else {
//                        bRun = true
//                    }
//                }
//            else -> {}
//        }
//    }
//
//    //接收数据线程
//    var readThread: Thread = object : Thread() {
//        override fun run() {
//            var num = 0
//            val buffer = ByteArray(1024)
//            val buffer_new = ByteArray(1024)
//            var i = 0
//            var n = 0
//            var end = 0
//            bRun = true
//
//            while (true) {
//                try {
//                    while (`is`!!.available() == 0) {
//                        while (bRun == false) {
//                        }
//                    }
//                    //此程序段用来防止读数不全
//                    while (true) {
//                        if (!bThread) //跳出循环
//                            return
//                        num=0
//                        end=`is`!!.read()
//                        while((end != 0x0a))//换行符为结束符
//                        {
//                            if(end != -1){
//                                buffer[num]= end.toByte()
//                                num++
//                            }
//                            end=`is`!!.read()
//                        }
//                        buffer[num]=0x0a //补上换行符
//                        num++
//                        //此程序段用来去除发送新行产生的回车
//                        val s0 = String(buffer, 0, num) //调试用
//                        n = 0
//                        i = 0
//                            while (i < num)
//                            {
//                                if (buffer[i].toInt() == 0x0d && buffer[i + 1].toInt() == 0x0a)
//                                {
//                                    buffer_new[n] = 0x0a//去回车
//                                    i++
//                                }
//                                else
//                                {
//                                    buffer_new[n] = buffer[i]
//                                }
//                                n++
//                                i++
//                            }
//                        val s = String(buffer_new, 0, n-1)
//                        //
//                        smsg =  s //写入接收缓存
//                        //mFrag1?.onReceiveData(smsg)
//
//                        handler.post(object: Runnable{
//                            override fun run() {
//                                runOnUiThread {
///*                              tiaoshi=findViewById(R.id.tiaoshi)
//                                tiaoshi!!.text= smsg*/
//                                    mFrag1?.onReceiveData(smsg)
//                                }
//                                handler.post(this)
//                            }
//                        })
//                        if (`is`!!.available() == 0) break //短时间没有数据才跳出进行显示
//                    }
///*                    handler.post(object: Runnable{
//                        override fun run() {
//                            runOnUiThread {
///*                              tiaoshi=findViewById(R.id.tiaoshi)
//                                tiaoshi!!.text= smsg*/
//                                mFrag1?.onReceiveData(smsg)
//                            }
//                            handler.post(this)
//                        }
//                    }) */
//                    handler.post(object : Runnable {
//                        override fun run() {
//                            runOnUiThread {
//                                val bundle = Bundle()
//                                bundle.putString("data", smsg)
//                                mFrag2?.setArguments(bundle)
//                                mFrag2?.onReceiveData1()
//                            }
//                            handler.post(this)
//                        }
//                    })
//                } catch (e: IOException) {
//                }
//            }
//        }
//    }
//
//    //消息处理队列
//    var handler: Handler = @SuppressLint("HandlerLeak")
//    object : Handler() {
//    }
//
//    private fun initEvents() {
//        //初始化四个Tab的点击事件
//        mTab1!!.setOnClickListener(this)
//        mTab2!!.setOnClickListener(this)
//        mTab3!!.setOnClickListener(this)
//
//    }
//    private fun initViews() {
//        //初始化两个个Tab的布局文件
//        mTab1 = findViewById<View>(R.id.id_tab1) as LinearLayout
//        mTab2 = findViewById<View>(R.id.id_tab2) as LinearLayout
//        mTab3 = findViewById<View>(R.id.id_tab3) as LinearLayout
//        //初始化两个Button
//        mImg1 = findViewById<Button>(R.id.id_tab_img1)
//        mImg2 = findViewById<Button>(R.id.id_tab_img2)
//        connect = findViewById<Button>(R.id.connect)
//
//    }
//    //处理Tab的点击事件
//    @SuppressLint("NonConstantResourceId")
//    override fun onClick(v: View) {
//        resetImgs() //先将两个ImageButton置为白色
//        when (v.id) {
//            R.id.id_tab1 -> selectTab(0)
//            R.id.id_tab2 -> selectTab(1)
//            R.id.id_tab3 -> startActivity(intent.setClass(this@FragmentmainActivity, BTActivity::class.java))
//        }
//    }
//
//    private fun selectTab(i: Int) {
//        //获取FragmentManager对象
//        val manager = supportFragmentManager
//        //获取FragmentTransaction对象
//        val transaction = manager.beginTransaction()
//        //先隐藏所有的Fragment
//        hideFragments(transaction)
//        when (i) {
//            0 -> {
//                mImg1!!.setTextColor(getColor(R.color.teal_700))
//                mImg2!!.setTextColor(getColor(R.color.white))
//                //如果第一页对应的Fragment没有实例化，则进行实例化，并显示出来
//                if (mFrag1 == null) {
//                    mFrag1 = Mainfragment()
//                    transaction.setCustomAnimations(
//                        R.anim.slide_in_from_right,
//                        R.anim.slide_out_to_left
//                    )
//                    transaction.add(R.id.fragment_container, mFrag1!!)
//                } else {
//                    //如果第一页对应的Fragment已经实例化，则直接显示出来
//                    transaction.setCustomAnimations(
//                        R.anim.slide_in_from_right,
//                        R.anim.slide_out_to_left
//                    )
//                    transaction.show(mFrag1!!)
//                }
//            }
//
//            1 -> {
//                mImg2!!.setTextColor(getColor(R.color.teal_700))
//                mImg1!!.setTextColor(getColor(R.color.white))
//                if (mFrag2 == null) {
//                    mFrag2 = Chatfragment()
//                    transaction.setCustomAnimations(
//                        R.anim.slide_in_from_right,
//                        R.anim.slide_out_to_left
//                    )
//                    transaction.add(R.id.fragment_container, mFrag2!!)
//                } else {
//                    transaction.setCustomAnimations(
//                        R.anim.slide_in_from_right,
//                        R.anim.slide_out_to_left
//                    )
//                    transaction.show(mFrag2!!)
//                }
//            }
//        }
//        //不要忘记提交事务
//        transaction.commit()
//    }
//
//    private fun hideFragments(transaction: FragmentTransaction) {
//        if (mFrag1 != null) {
//            transaction.hide(mFrag1!!)
//        }
//        if (mFrag2 != null) {
//            transaction.hide(mFrag2!!)
//        }
//    }
//
//    private fun resetImgs() {
//        mImg1!!.setTextColor(getColor(R.color.white))
//        mImg2!!.setTextColor(getColor(R.color.white))
//    }
//
//    //关闭程序掉用处理部分
//    override fun onDestroy() {
//        super.onDestroy()
//        if (mBluetoothSocket != null) //关闭连接socket
//            try {
//                mBluetoothSocket!!.close()
//            } catch (e: IOException) {
//            }
//        //	_bluetooth.disable();  //关闭蓝牙服务
//    }
//}
//
//
//
