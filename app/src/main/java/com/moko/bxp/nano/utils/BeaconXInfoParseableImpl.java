package com.moko.bxp.nano.utils;

import android.os.ParcelUuid;
import android.os.SystemClock;
import android.text.TextUtils;

import com.moko.ble.lib.utils.MokoUtils;
import com.moko.bxp.nano.entity.BeaconXInfo;
import com.moko.support.nano.entity.DeviceInfo;
import com.moko.support.nano.service.DeviceInfoParseable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class BeaconXInfoParseableImpl implements DeviceInfoParseable<BeaconXInfo> {
    private HashMap<String, BeaconXInfo> beaconXInfoHashMap;

    public BeaconXInfoParseableImpl() {
        this.beaconXInfoHashMap = new HashMap<>();
    }

    @Override
    public BeaconXInfo parseDeviceInfo(DeviceInfo deviceInfo) {
        int battery = -1;
        ScanResult result = deviceInfo.scanResult;
        ScanRecord record = result.getScanRecord();
        // filter
        boolean isEddystone = false;
        boolean isBeacon = false;
        boolean isSensorInfo = false;
        byte[] values = null;
        int type = -1;
        String tagId = "";
        if (null == record) return null;
        Map<ParcelUuid, byte[]> map = record.getServiceData();
        byte[] manufacturerBytes = record.getManufacturerSpecificData(0x004C);
        if (null != manufacturerBytes && manufacturerBytes.length == 23) {
            isBeacon = true;
//            if (manufacturerBytes.length != 23) return null;
            type = BeaconXInfo.VALID_DATA_FRAME_TYPE_IBEACON_APPLE;
            values = manufacturerBytes;
        }
        if (map != null && !map.isEmpty()) {
            Iterator iterator = map.keySet().iterator();
            if (iterator.hasNext()) {
                ParcelUuid parcelUuid = (ParcelUuid) iterator.next();
                if (parcelUuid.toString().startsWith("0000feaa")) {
                    isEddystone = true;
                    byte[] bytes = map.get(parcelUuid);
                    if (bytes != null) {
                        switch (bytes[0] & 0xff) {
                            case BeaconXInfo.VALID_DATA_FRAME_TYPE_UID:
                                if (bytes.length != 20)
                                    return null;
                                type = BeaconXInfo.VALID_DATA_FRAME_TYPE_UID;
                                // 00ee0102030405060708090a0102030405060000
                                break;
                            case BeaconXInfo.VALID_DATA_FRAME_TYPE_TLM:
                                if (bytes.length != 14)
                                    return null;
                                type = BeaconXInfo.VALID_DATA_FRAME_TYPE_TLM;
                                // 20000d18158000017eb20002e754
                                break;
                        }
                    }
                    values = bytes;
                } else if (parcelUuid.toString().startsWith("0000ea01")) {
                    isSensorInfo = true;
                    byte[] bytes = map.get(parcelUuid);
                    if (bytes != null) {
                        switch (bytes[0] & 0xff) {
                            case BeaconXInfo.VALID_DATA_FRAME_TYPE_SENSOR_INFO:
                                if (bytes.length < 19)
                                    return null;
                                type = BeaconXInfo.VALID_DATA_FRAME_TYPE_SENSOR_INFO;
                                battery = MokoUtils.toInt(Arrays.copyOfRange(bytes, 16, 18));
                                tagId = MokoUtils.bytesToHexString(Arrays.copyOfRange(bytes, 18, bytes.length));
                                break;
                        }
                    }
                    values = bytes;
                }
            }
        }
        if ((!isEddystone && !isBeacon && !isSensorInfo) || values == null || type == -1) {
            return null;
        }
        // avoid repeat
        BeaconXInfo beaconXInfo;
        if (beaconXInfoHashMap.containsKey(deviceInfo.mac)) {
            beaconXInfo = beaconXInfoHashMap.get(deviceInfo.mac);
            if (!TextUtils.isEmpty(tagId))
                beaconXInfo.tagId = tagId;
            beaconXInfo.rssi = deviceInfo.rssi;
            if (battery >= 0) {
                beaconXInfo.battery = battery;
            }
            beaconXInfo.scanRecord = deviceInfo.scanRecord;
            long currentTime = SystemClock.elapsedRealtime();
            long intervalTime = currentTime - beaconXInfo.scanTime;
            beaconXInfo.intervalTime = intervalTime;
            beaconXInfo.scanTime = currentTime;
        } else {
            beaconXInfo = new BeaconXInfo();
            beaconXInfo.tagId = tagId;
            beaconXInfo.mac = deviceInfo.mac;
            beaconXInfo.rssi = deviceInfo.rssi;
            if (battery < 0) {
                beaconXInfo.battery = -1;
            } else {
                beaconXInfo.battery = battery;
            }
            beaconXInfo.scanRecord = deviceInfo.scanRecord;
            beaconXInfo.scanTime = SystemClock.elapsedRealtime();
            beaconXInfo.validDataHashMap = new HashMap<>();
            beaconXInfoHashMap.put(deviceInfo.mac, beaconXInfo);
        }
        String data = MokoUtils.bytesToHexString(values);
        BeaconXInfo.ValidData validData = new BeaconXInfo.ValidData();
        validData.data = data;
        validData.type = type;

        // 广播帧有可变值以TYPE为KEY更新
        if (type == BeaconXInfo.VALID_DATA_FRAME_TYPE_TLM) {
            beaconXInfo.validDataHashMap.put(type + "", validData);
            return beaconXInfo;
        }
        if (type == BeaconXInfo.VALID_DATA_FRAME_TYPE_SENSOR_INFO) {
            beaconXInfo.validDataHashMap.put(type + "", validData);
            return beaconXInfo;
        }
        beaconXInfo.validDataHashMap.put(data, validData);
        return beaconXInfo;
    }
}
