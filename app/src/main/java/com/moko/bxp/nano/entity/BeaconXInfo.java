package com.moko.bxp.nano.entity;

import java.io.Serializable;
import java.util.HashMap;


public class BeaconXInfo implements Serializable {

    public static final int VALID_DATA_FRAME_TYPE_UID = 0x00;
    public static final int VALID_DATA_FRAME_TYPE_TLM = 0x20;
    public static final int VALID_DATA_FRAME_TYPE_IBEACON_APPLE = 0x02;
    public static final int VALID_DATA_FRAME_TYPE_NANO_INFO = 0x03;

    public String name;
    public int rssi;
    public String mac;
    public String scanRecord;
    public int battery;
    public long intervalTime;
    public long scanTime;
//    public int lastAdvType;
//    public int triggerStatus;
    public int lastCutoffStatus;
    public int cutoffStatus;
    public int lastBtnAlarmStatus;
    public int btnAlarmStatus;
    public HashMap<String, ValidData> validDataHashMap;

    @Override
    public String toString() {
        return "BeaconXInfo{" +
                ", mac='" + mac + '\'' +
                '}';
    }


    public static class ValidData {
        public int type;
        public byte[] values;
        public String data;

        @Override
        public String toString() {
            return "ValidData{" +
                    "type=" + type +
                    ", data='" + data + '\'' +
                    '}';
        }
    }
}
