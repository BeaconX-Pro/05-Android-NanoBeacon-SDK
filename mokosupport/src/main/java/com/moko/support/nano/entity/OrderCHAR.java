package com.moko.support.nano.entity;

import java.io.Serializable;
import java.util.UUID;

public enum OrderCHAR implements Serializable {
    // 180A
    CHAR_MODEL_NUMBER(UUID.fromString("00002A24-0000-1000-8000-00805F9B34FB")),
    CHAR_SERIAL_NUMBER(UUID.fromString("00002A25-0000-1000-8000-00805F9B34FB")),
    CHAR_MANUFACTURER_NAME(UUID.fromString("00002A29-0000-1000-8000-00805F9B34FB")),
    CHAR_FIRMWARE_REVISION(UUID.fromString("00002A26-0000-1000-8000-00805F9B34FB")),
    CHAR_HARDWARE_REVISION(UUID.fromString("00002A27-0000-1000-8000-00805F9B34FB")),
    CHAR_SOFTWARE_REVISION(UUID.fromString("00002A28-0000-1000-8000-00805F9B34FB")),
    // E62A0001
    CHAR_LOCKED_NOTIFY(UUID.fromString("E62A0003-1362-4F28-9327-F5B74E970801")),
    CHAR_THREE_AXIS_NOTIFY(UUID.fromString("E62A0008-1362-4F28-9327-F5B74E970801")),
    CHAR_TH_NOTIFY(UUID.fromString("E62A0009-1362-4F28-9327-F5B74E970801")),
    CHAR_STORE_NOTIFY(UUID.fromString("E62A000A-1362-4F28-9327-F5B74E970801")),
    CHAR_LIGHT_SENSOR_NOTIFY(UUID.fromString("E62A000B-1362-4F28-9327-F5B74E970801")),
    CHAR_LIGHT_SENSOR_CURRENT(UUID.fromString("E62A000C-1362-4F28-9327-F5B74E970801")),
    CHAR_PARAMS(UUID.fromString("E62A0002-1362-4F28-9327-F5B74E970801")),
    CHAR_DEVICE_TYPE(UUID.fromString("E62A0004-1362-4F28-9327-F5B74E970801")),
    CHAR_SLOT_TYPE(UUID.fromString("E62A0005-1362-4F28-9327-F5B74E970801")),
    CHAR_BATTERY(UUID.fromString("E62A0006-1362-4F28-9327-F5B74E970801")),
    CHAR_DISCONNECT(UUID.fromString("E62A0007-1362-4F28-9327-F5B74E970801")),
    // A3C87500
    CHAR_ADV_SLOT(UUID.fromString("A3C87502-8ED3-4BDF-8A39-A01BEBEDE295")),
    CHAR_ADV_INTERVAL(UUID.fromString("A3C87503-8ED3-4BDF-8A39-A01BEBEDE295")),
    CHAR_RADIO_TX_POWER(UUID.fromString("A3C87504-8ED3-4BDF-8A39-A01BEBEDE295")),
    CHAR_ADV_TX_POWER(UUID.fromString("A3C87505-8ED3-4BDF-8A39-A01BEBEDE295")),
    CHAR_LOCK_STATE(UUID.fromString("A3C87506-8ED3-4BDF-8A39-A01BEBEDE295")),
    CHAR_UNLOCK(UUID.fromString("A3C87507-8ED3-4BDF-8A39-A01BEBEDE295")),
    CHAR_ADV_SLOT_DATA(UUID.fromString("A3C8750A-8ED3-4BDF-8A39-A01BEBEDE295")),
    CHAR_RESET_DEVICE(UUID.fromString("A3C8750B-8ED3-4BDF-8A39-A01BEBEDE295")),
    CHAR_CONNECTABLE(UUID.fromString("A3C8750C-8ED3-4BDF-8A39-A01BEBEDE295")),
    ;

    private UUID uuid;

    OrderCHAR(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
