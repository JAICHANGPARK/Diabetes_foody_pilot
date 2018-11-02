package com.dreamwalker.diabetesfoodypilot.activity.accessory.foodtray.realtime

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.consts.IntentConst
import android.Manifest
import android.animation.Animator
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import butterknife.ButterKnife
import com.dreamwalker.diabetesfoodypilot.R
import com.dreamwalker.diabetesfoodypilot.activity.IActivityBaseSetting
import com.dreamwalker.diabetesfoodypilot.adapter.accessory.realtime.RealTimeAdapter
import com.dreamwalker.diabetesfoodypilot.model.accessory.RealTime
import com.dreamwalker.diabetesfoodypilot.service.foodtray.RealTimeBluetoothLeService
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_real_time.*
import org.jetbrains.anko.toast
import java.security.MessageDigest
import java.util.logging.Logger

class RealTimeActivity : AppCompatActivity(), IActivityBaseSetting {

    private val REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 1000


    lateinit var mDeviceAddress: String
    private var mBluetoothLeService: RealTimeBluetoothLeService? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private lateinit var bluetoothManager: BluetoothManager

    private var mConnected = false

    lateinit var realTimeAdapter: RealTimeAdapter
    lateinit var realTimeList: ArrayList<RealTime>
    lateinit var entries: ArrayList<Entry>
    lateinit var dataSet: LineDataSet
    var count = 0


    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            val binder = service as RealTimeBluetoothLeService.LocalBinder
            mBluetoothLeService = binder.service
            if (!mBluetoothLeService!!.initialize()) {
                finish()
            }
            mBluetoothLeService!!.connect(mDeviceAddress)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBluetoothLeService = null
        }
    }


    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (RealTimeBluetoothLeService.ACTION_GATT_CONNECTED == action) {
                mConnected = true
//                textView.append("연결됨" + "\n")
//                updateConnectionState(R.string.connected)
                invalidateOptionsMenu()
            } else if (RealTimeBluetoothLeService.ACTION_GATT_DISCONNECTED == action) {
                mConnected = false
//                textView.append("연결 해제"+ "\n")
//                updateConnectionState(R.string.disconnected)
                invalidateOptionsMenu()
//                clearUI()
            } else if (RealTimeBluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED == action) {
                // Show all the supported services and characteristics on the user interface.
//                displayGattServices(mBluetoothLeService.getSupportedGattServices())
//                textView.append("서비스 특성 탐색 완료" + "\n")
            } else if (RealTimeBluetoothLeService.ACTION_DATA_AVAILABLE == action) {
                val value = intent.getStringExtra(RealTimeBluetoothLeService.EXTRA_DATA)
                entries.add(Entry(count.toFloat(), value.toFloat()))
                realTimeList.clear()
                realTimeList.add(RealTime("밥", "쌀밥", intent.getStringExtra(RealTimeBluetoothLeService.EXTRA_DATA)))
                realTimeList.add(RealTime("국", "된장국", intent.getStringExtra(RealTimeBluetoothLeService.EXTRA_DATA)))
                realTimeList.add(RealTime("반찬A", "감자조림", intent.getStringExtra(RealTimeBluetoothLeService.EXTRA_DATA)))
                realTimeList.add(RealTime("반찬B", "배추김치", intent.getStringExtra(RealTimeBluetoothLeService.EXTRA_DATA)))
                realTimeList.add(RealTime("반찬C", "샐러드", intent.getStringExtra(RealTimeBluetoothLeService.EXTRA_DATA)))
                realTimeList.add(RealTime("반찬D", "제육볶음", intent.getStringExtra(RealTimeBluetoothLeService.EXTRA_DATA)))
                realTimeAdapter.notifyDataSetChanged()
                count++

                dataSet = LineDataSet(entries, "Label")
                val lineData = LineData(dataSet)
                line_chart.data = lineData
                line_chart.invalidate() // refresh
//                textView.append(intent.getStringExtra(RealTimeBluetoothLeService.EXTRA_DATA) + "\n")
//                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
            } else if (RealTimeBluetoothLeService.ACTION_REAL_TIME_START_PHASE == action) {
//                Toast.makeText(applicationContext, "인증 시작 ", Toast.LENGTH_SHORT).show()
            } else if (RealTimeBluetoothLeService.ACTION_REAL_TIME_FIRST_PHASE == action) {
//                Toast.makeText(applicationContext, "암호화 인증 완료 ", Toast.LENGTH_SHORT).show()
            } else if (RealTimeBluetoothLeService.ACTION_REAL_TIME_SECOND_PHASE == action) {
//                Toast.makeText(applicationContext, "시간 동기화 완료 ", Toast.LENGTH_SHORT).show()
            } else if (RealTimeBluetoothLeService.ACTION_REAL_TIME_FINAL_PHASE == action) {
                toast("모든 인증 처리 완료 ")
                animation_view.setAnimation(R.raw.process_complete)
                animation_view.repeatCount = 0
                animation_view.playAnimation()
                val listener = object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                        toast("onAnimationRepeat ")
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        toast("onAnimationCancel ")
                        animation_layout.visibility = View.GONE
                        data_layout.visibility = View.VISIBLE
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        toast("onAnimationStart ")
                        val msg = "인증 완료! 최적화 중... \n 잠시만 기다려주세요"
                        animation_text_view.text = msg

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        toast("완료 모두 완료 ")
                        animation_view.cancelAnimation()
                    }

                }

                animation_view.addAnimatorListener(listener)
//                Toasty.success(context, "모든 인증 처리 완료 ", Toast.LENGTH_SHORT).show()
//                Toast.makeText(applicationContext, "모든 인증 처리 완료 ", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time)
        setSupportActionBar(tool_bar)
        tool_bar.setNavigationOnClickListener {
            val builder = AlertDialog.Builder(this@RealTimeActivity)
            builder.setTitle("알림")
            builder.setMessage("장비(지능형 식판) 검색을 종료하시겠어요?")
            builder.setPositiveButton(android.R.string.yes) { _, _ -> finish() }
            builder.setNegativeButton(android.R.string.no) { dialog, _ -> dialog.dismiss() }
            builder.show()
        }
        initSetting()

        with(recyclerView) {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this.context, 2, GridLayoutManager.VERTICAL, false)
            adapter = realTimeAdapter
        }

        animation_layout.visibility = View.VISIBLE
        data_layout.visibility = View.GONE

        entries = ArrayList()
        realTimeList = ArrayList()
        realTimeAdapter = RealTimeAdapter(this, realTimeList)

        mDeviceAddress = intent.getStringExtra(IntentConst.REAL_TIME_SCAN_PAGE)
        toast(mDeviceAddress)
        checkPermission()
        checkBLESupport()
        val checkBluetoothEnableFlag = checkBluetoothEnable()

        if (checkBluetoothEnableFlag) {
            val gattServiceIntent = Intent(this, RealTimeBluetoothLeService::class.java)
            bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }


        val hashs = sha256("Hello Qiita!")
        Logger.getLogger(this::class.java.name).warning(hashs)

    }

    override fun bindView() {
        ButterKnife.bind(this)
    }

    override fun initSetting() {
//        bindView()
        initToasty()
    }

    fun initToasty() {
        Toasty.Config.getInstance().apply();
    }

    fun sha256(input: String) = hashString("SHA-256", input)

    private fun hashString(type: String, input: String): String {
        val HEX_CHARS = "0123456789ABCDEF"
        val bytes = MessageDigest
                .getInstance(type)
                .digest(input.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }


    override fun onResume() {
        super.onResume()

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
        if (mBluetoothLeService != null) {
            val result = mBluetoothLeService!!.connect(mDeviceAddress)
            Log.d("Mains", "Connect request result=$result")
        }

    }

    override fun onPause() {
        super.onPause()
        // TODO: 2018-07-06 브로드케스트 리시버는 꼭 해제해준다. 하지 않게되면 메모리 점유가 진행되어 메모리 예외가 발생한다. - 박제창
        unregisterReceiver(mGattUpdateReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()

        // TODO: 2018-07-06 서비스는 꼭 unbind 해준다. - 박제창
        unbindService(mServiceConnection)
        mBluetoothLeService = null
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(RealTimeBluetoothLeService.ACTION_GATT_CONNECTED)
        intentFilter.addAction(RealTimeBluetoothLeService.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(RealTimeBluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(RealTimeBluetoothLeService.ACTION_DATA_AVAILABLE)

        intentFilter.addAction(RealTimeBluetoothLeService.ACTION_REAL_TIME_START_PHASE)
        intentFilter.addAction(RealTimeBluetoothLeService.ACTION_REAL_TIME_FIRST_PHASE)
        intentFilter.addAction(RealTimeBluetoothLeService.ACTION_REAL_TIME_SECOND_PHASE)
        intentFilter.addAction(RealTimeBluetoothLeService.ACTION_REAL_TIME_FINAL_PHASE)
        return intentFilter
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSION_ACCESS_COARSE_LOCATION)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_ACCESS_COARSE_LOCATION ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "스캔 권한을 얻었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "권한이 거부됬습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    private fun checkBLESupport() {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun checkBluetoothEnable(): Boolean {
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        // Checks if Bluetooth is supported on the device.
        return if (mBluetoothAdapter == null) {
//            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show()
            finish()
            false
        } else {
            true
        }

    }
}
