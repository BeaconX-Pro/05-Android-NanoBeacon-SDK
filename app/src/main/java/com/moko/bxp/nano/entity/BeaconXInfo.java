package com.moko.bxp.nano.entity;

import java.io.Serializable;
import java.util.HashMap;


public class BeaconXInfo implements Serializable {

    public static final int VALID_DATA_FRAME_TYPE_UID = 0x00;
    public static final int VALID_DATA_FRAME_TYPE_TLM = 0x20;
    public static final int VALID_DATA_FRAME_TYPE_IBEACON_APPLE = 0x02;
    public static final int VALID_DATA_FRAME_TYPE_SENSOR_INFO = 0x82;


    public String tagId;
    public int rssi;
    public String mac;
    public String scanRecord;
    public int battery;
    public long intervalTime;
    public long scanTime;
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
