package com.moko.bxp.nano.utils;

import android.text.TextUtils;

import com.moko.ble.lib.utils.MokoUtils;
import com.moko.bxp.nano.entity.BeaconXTLM;
import com.moko.bxp.nano.entity.BeaconXUID;
import com.moko.bxp.nano.entity.BeaconXiBeacon;
import com.moko.bxp.nano.entity.SensorInfo;


public class BeaconXParser {

    public static BeaconXUID getUID(String data) {
        // 00ee0102030405060708090a0102030405060000
        BeaconXUID uid = new BeaconXUID();
        int rssi_0m = Integer.parseInt(data.substring(2, 4), 16);
        uid.rangingData = (byte) rssi_0m + "";
        uid.namespace = data.substring(4, 24).toUpperCase();
        uid.instanceId = data.substring(24, 36).toUpperCase();
        return uid;
    }

    public static BeaconXTLM getTLM(String data) {
        // 20000d18158000017eb20002e754
        BeaconXTLM tlm = new BeaconXTLM();
        tlm.vbatt = Integer.parseInt(data.substring(4, 8), 16) + "";
        int temp1 = Integer.parseInt(data.substring(8, 10), 16);
        int temp2 = Integer.parseInt(data.substring(10, 12), 16);
        int tempInt = temp1 > 128 ? temp1 - 256 : temp1;
        float tempDecimal = temp2 / 256.0f;
        float temperature = tempInt + tempDecimal;
        String tempStr = MokoUtils.getDecimalFormat("0.0").format(temperature);
        tlm.temp = String.format("%s°C", tempStr);
        tlm.adv_cnt = Long.parseLong(data.substring(12, 20), 16) + "";
        float seconds = Long.parseLong(data.substring(20, 28), 16) * 0.1f;
        int day = 0, hours = 0, minutes = 0;
        day = (int) (seconds / (60 * 60 * 24));
        seconds -= day * 60 * 60 * 24;
        hours = (int) (seconds / (60 * 60));
        seconds -= hours * 60 * 60;
        minutes = (int) (seconds / 60);
        seconds -= minutes * 60;
        tlm.sec_cnt = String.format("%dd%dh%dm%ss", day, hours, minutes, MokoUtils.getDecimalFormat("0.0").format(seconds));
        return tlm;
    }

    public static BeaconXiBeacon getiBeacon(int rssi, String data) {
        // 50ee0c0102030405060708090a0b0c0d0e0f1000010002
        BeaconXiBeacon iBeacon = new BeaconXiBeacon();
        StringBuilder stringBuilder = new StringBuilder(data.substring(4, 36).toLowerCase());
        stringBuilder.insert(8, "-");
        stringBuilder.insert(13, "-");
        stringBuilder.insert(18, "-");
        stringBuilder.insert(23, "-");
        iBeacon.uuid = stringBuilder.toString();
        iBeacon.major = Integer.parseInt(data.substring(36, 40), 16) + "";
        iBeacon.minor = Integer.parseInt(data.substring(40, 44), 16) + "";
        int rssi_1m = Integer.parseInt(data.substring(44, 46), 16);
        iBeacon.rangingData = (byte) rssi_1m + "";
        double distance = MokoUtils.getDistance(rssi, Math.abs((byte) rssi_1m));
        String distanceDesc = "Unknown";
        if (distance <= 0.1) {
            distanceDesc = "Immediate";
        } else if (distance > 0.1 && distance <= 1.0) {
            distanceDesc = "Near";
        } else if (distance > 1.0) {
            distanceDesc = "Far";
        }
        iBeacon.distanceDesc = distanceDesc;
        return iBeacon;
    }

    public static SensorInfo getSensorInfo(String data) {
        SensorInfo sensorInfo = new SensorInfo();
        int temp = Integer.parseInt(data.substring(24, 28), 16);
        if (temp != 0xFFFF) {
            temp = temp >> 4;
            String tempStr = MokoUtils.getDecimalFormat("0.#").format(temp * 0.0625f);
            sensorInfo.temperature = String.format("%s°C", tempStr);
        } else {
            sensorInfo.temperature = String.format("%s°C", "N/A");
        }
        return sensorInfo;
    }
}
