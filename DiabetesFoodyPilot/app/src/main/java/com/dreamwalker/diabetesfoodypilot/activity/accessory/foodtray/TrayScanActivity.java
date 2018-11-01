package com.dreamwalker.diabetesfoodypilot.activity.accessory.foodtray;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dreamwalker.diabetesfoodypilot.R;

public class TrayScanActivity extends AppCompatActivity  {

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner bluetoothLeScanner;

    Handler handler;

    boolean mScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tray_scan);
    }
}
