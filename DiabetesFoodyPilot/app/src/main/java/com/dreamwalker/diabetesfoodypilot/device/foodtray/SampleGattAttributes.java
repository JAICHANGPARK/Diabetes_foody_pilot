/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dreamwalker.diabetesfoodypilot.device.foodtray;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public final static UUID BLE_SERVICE_GLUCOSE		= UUID.fromString("00001808-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_SERVICE_DEVICE_INFO	= UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_SERVICE_CUSTOM     	= UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");

    //Characteristic
    public final static UUID BLE_CHAR_GLUCOSE_SERIALNUM	= UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_SOFTWARE_REVISION	= UUID.fromString("00002A28-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_GLUCOSE_MEASUREMENT= UUID.fromString("00002A18-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_GLUCOSE_CONTEXT	= UUID.fromString("00002A34-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_GLUCOSE_RACP		= UUID.fromString("00002A52-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_CUSTOM_TIME	    = UUID.fromString("0000FFF1-0000-1000-8000-00805f9b34fb");

    public final static UUID BLE_CHAR_CUSTOM	    = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"); // 쓰기 , 보내는 특성
    public final static UUID BLE_CHAR_CUSTOM_TRAY	    = UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb"); // 노티
    public final static UUID BLE_CHAR_CUSTOM_REALTIME	    = UUID.fromString("0000ffe3-0000-1000-8000-00805f9b34fb"); // 노티

    //Descriptor
    public final static UUID BLE_DESCRIPTOR_DESCRIPTOR	= UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
