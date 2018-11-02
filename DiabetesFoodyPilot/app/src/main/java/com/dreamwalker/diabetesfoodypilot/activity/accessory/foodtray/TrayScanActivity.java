package com.dreamwalker.diabetesfoodypilot.activity.accessory.foodtray;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.activity.IActivityBaseSetting;
import com.dreamwalker.diabetesfoodypilot.activity.accessory.foodtray.realtime.RealTimeActivity;
import com.dreamwalker.diabetesfoodypilot.adapter.accessory.scan.DeviceItemClickListener;
import com.dreamwalker.diabetesfoodypilot.adapter.accessory.scan.DeviceScanAdapterV2;
import com.dreamwalker.diabetesfoodypilot.model.accessory.Device;

import java.util.ArrayList;
import java.util.List;

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.consts.IntentConst;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;
import sha256.com.dreamwalker.multiwaveview.MultiWaveHeader;

public class TrayScanActivity extends AppCompatActivity implements IActivityBaseSetting, DeviceItemClickListener {
    private static final String TAG = "TrayScanActivity";


    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.waveHeader)
    MultiWaveHeader multiWaveHeader;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1000;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000; // Stops scanning after 10 seconds.

    private int pageNumber;

    ArrayList<BluetoothDevice> bleDeviceList;
    ArrayList<ScanResult> scanResultArrayList;

    DeviceScanAdapterV2 adapter;


    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner bluetoothLeScanner;

    Handler handler;

    boolean mScanning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tray_scan);
        initSetting();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TrayScanActivity.this);
            builder.setTitle("알림");
            builder.setMessage("장비(지능형 식판) 검색을 종료하시겠어요?");
            builder.setPositiveButton(android.R.string.yes, (dialog, which) -> finish());
            builder.setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        bleDeviceList = new ArrayList<>();
        scanResultArrayList = new ArrayList<>();
        handler = new Handler();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        checkScanPermission();
        checkBleSupport();
        getBluetoothAdapter();
        checkBluetoothSupport();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
//            if (audioManager.getRingerMode() != AudioManager.ADJUST_MUTE){
//                mediaPlayer.start();
//            }
            adapter = new DeviceScanAdapterV2(bleDeviceList, scanResultArrayList, this);
            recyclerView.setAdapter(adapter);
            scanLeDevice(true);
//            StateButton.setText("STOP");
        }

        adapter.setDeviceItemClickListener(this);
    }

    @Override
    public void bindView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initSetting() {
        bindView();
        getIntentValue();
        initToasty();
        initWaveView();
        initPaper();
    }

    private void getIntentValue() {
        pageNumber = getIntent().getIntExtra(IntentConst.DEVICE_SCAN, 0);

    }

    private void initWaveView() {
        multiWaveHeader.setScaleY(-1f);
    }

    private void initToasty() {
        Toasty.Config.getInstance().apply();
    }

    private void initPaper() {
        Paper.init(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkScanPermission() {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.permission_alert_title));
            builder.setMessage(getString(R.string.permission_alert_message));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(dialog -> requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION));
            builder.show();
        }
    }

    private void checkBleSupport() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toasty.info(this, R.string.ble_not_supported + "", Toast.LENGTH_SHORT, true).show();
            finish();
        }
    }

    private void getBluetoothAdapter() {
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    private void checkBluetoothSupport() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (!bluetoothAdapter.isEnabled()) { // TODO: 2018-10-06 블루투스 오류 처리 하기

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        } else {

            if (enable) {
                handler.postDelayed(() -> {
                    Log.e(TAG, "scanLeDevice: ---> " + "Scan Handler Done!!");
                    mScanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                    invalidateOptionsMenu();
//                        StateButton.setText("SCAN");
//                        animationView.cancelAnimation();
//                        animationView.setFrame(0);
                }, SCAN_PERIOD);
                mScanning = true;
                startNEWBTLEDiscovery();
                invalidateOptionsMenu();
                //bluetoothLeScanner.startScan(leScanCallback);
            } else {
                mScanning = false;
                bluetoothLeScanner.stopScan(leScanCallback);
                invalidateOptionsMenu();
            }
        }

    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
//            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();


            // TODO: 2018-10-12 중복 방지
            if (bleDeviceList.size() < 1) {
                bleDeviceList.add(device);
                scanResultArrayList.add(result);
                adapter.notifyDataSetChanged();
            } else {
                boolean flag = true;
                for (int i = 0; i < bleDeviceList.size(); i++) {
                    if (device.getAddress().equals(bleDeviceList.get(i).getAddress())) {
                        flag = false;
                    }
                }
                if (flag) {
                    bleDeviceList.add(device);
                    scanResultArrayList.add(result);
                    adapter.notifyDataSetChanged();
                }
            }

            //String address = device.getAddress();
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


    // New BTLE Discovery use startScan (List<ScanFilter> filters,
    //                                  ScanSettings settings,
    //                                  ScanCallback callback)
    // It's added on API21
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startNEWBTLEDiscovery() {
        // Only use new API when user uses Lollipop+ device
        if (bluetoothLeScanner == null) {
            getBluetoothAdapter();
        } else {
            bluetoothLeScanner.startScan(getScanFilters(), getScanSettings(), leScanCallback);
        }

    }

    private List<ScanFilter> getScanFilters() {
        List<ScanFilter> allFilters = new ArrayList<>();
        ScanFilter scanFilter0 = new ScanFilter.Builder().setDeviceName("SmartTray").build();
        allFilters.add(scanFilter0);
//        ScanFilter scanFilter1 = new ScanFilter.Builder().setDeviceName("").build();
//        for (DeviceCoordinator coordinator : DeviceHelper.getInstance().getAllCoordinators()) {
//            allFilters.addAll(coordinator.createBLEScanFilters());
//        }
        return allFilters;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ScanSettings getScanSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new ScanSettings.Builder()
                    .setScanMode(android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setMatchMode(android.bluetooth.le.ScanSettings.MATCH_MODE_STICKY)
                    .build();
        } else {
            return new ScanSettings.Builder()
                    .setScanMode(android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_device_scan, menu);

        if (!mScanning) {
            menu.findItem(R.id.cancel).setVisible(false);
            menu.findItem(R.id.scan).setVisible(true);
        } else {
            menu.findItem(R.id.cancel).setVisible(true);
            menu.findItem(R.id.scan).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                bleDeviceList.clear();
                scanResultArrayList.clear();
                adapter.notifyDataSetChanged();
                scanLeDevice(true);
                break;

            case R.id.cancel:
                scanLeDevice(false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO: 2018-11-02 권한이 허가되면 스캔을 시작합니다.
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, " +
                            "this app will not be able to discover BLE Device when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog -> finish());
                    builder.show();
                }
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
//            adapter = new DeviceScanAdapter(bleDeviceList, this);
//            recyclerView.setAdapter(adapter);
//            scanLeDevice(true);
//            StateButton.setText("STOP");
        }
    }

    @Override
    protected void onPause() {
        scanLeDevice(false);
        bleDeviceList.clear();
        adapter.notifyDataSetChanged();
        super.onPause();
    }

    @Override
    public void onItemClick(View v, int position) {
        Log.e(TAG, "onItemClick: " + position);
        String deviceAddress = bleDeviceList.get(position).getAddress();
        String deviceName = bleDeviceList.get(position).getName();
        ArrayList<Device> userDeviceList = new ArrayList<>();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        switch (pageNumber) {
            case 0:
                userDeviceList.add(new Device(deviceName, deviceAddress));
                builder.setTitle("알림...");
                builder.setMessage(deviceName + "의 엑세서리를 등록합니다.");
                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    Paper.book("user").write("device", userDeviceList);
                    finish();
                });
                builder.show();
                break;
            case 1: //데이터 동기화
                builder.setTitle("알림..");
                builder.setMessage(deviceName + "의 엑세서리를 통해 데이터를 동기화 합니다.");
                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {

                });
                builder.show();
                break;
            case 2: //실시간 정보 동기화
                builder.setTitle("알림..");
                builder.setMessage(deviceName + "의 엑세서리를 통해 실시간 데이터를 확인합니다.");
                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    Intent intent = new Intent(TrayScanActivity.this, RealTimeActivity.class);
                    intent.putExtra(IntentConst.REAL_TIME_SCAN_PAGE, deviceAddress);
                    startActivity(intent);
                });
                builder.show();
                break;
        }


    }

    @Override
    public void onItemLongClick(View v, int position) {

    }
}
