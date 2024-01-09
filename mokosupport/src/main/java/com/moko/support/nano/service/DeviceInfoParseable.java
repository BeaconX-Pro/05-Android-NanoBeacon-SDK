package com.moko.support.nano.service;

import com.moko.support.nano.entity.DeviceInfo;

/**
 * @Date 2018/1/11
 * @Author wenzheng.liu
 * @Description 设备解析接口
 * @ClassPath com.moko.support.nano.service.DeviceInfoParseable
 */
public interface DeviceInfoParseable<T> {
    T parseDeviceInfo(DeviceInfo deviceInfo);
}
