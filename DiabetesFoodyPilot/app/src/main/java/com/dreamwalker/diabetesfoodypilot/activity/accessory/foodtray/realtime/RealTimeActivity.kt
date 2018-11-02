package com.dreamwalker.diabetesfoodypilot.activity.accessory.foodtray.realtime

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.consts.IntentConst
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import butterknife.ButterKnife
import com.dreamwalker.diabetesfoodypilot.R
import com.dreamwalker.diabetesfoodypilot.activity.IActivityBaseSetting
import com.dreamwalker.diabetesfoodypilot.service.foodtray.RealTimeBluetoothLeService
import org.jetbrains.anko.toast
import java.security.MessageDigest
import java.util.logging.Logger

class RealTimeActivity : AppCompatActivity(), IActivityBaseSetting {

    private val REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 1000


    lateinit var mDeviceAddress: String
    private var mBluetoothLeService: RealTimeBluetoothLeService? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    lateinit var bluetoothManager: BluetoothManager

    private var mConnected = false

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
            if (RealTimeBluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true
//                textView.append("연결됨" + "\n")
//                updateConnectionState(R.string.connected)
                invalidateOptionsMenu()
            } else if (RealTimeBluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false
//                textView.append("연결 해제"+ "\n")
//                updateConnectionState(R.string.disconnected)
                invalidateOptionsMenu()
//                clearUI()
            } else if (RealTimeBluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
//                displayGattServices(mBluetoothLeService.getSupportedGattServices())
//                textView.append("서비스 특성 탐색 완료" + "\n")
            } else if (RealTimeBluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                textView.append(intent.getStringExtra(RealTimeBluetoothLeService.EXTRA_DATA) + "\n")
//                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time)

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
        bindView()
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
