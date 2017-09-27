package com.sundy.iman.paperdb;

import io.paperdb.Paper;

/**
 * 存放处理一些常量数据 Paper
 * Created by sundy on 17/9/27.
 */

public class CommonDataPaper {

    /**
     * 保存设备ID
     */
    public static void saveDeviceId(String deviceId) {
        Paper.book().write("DeviceId", deviceId);
    }

    /**
     * 获取设备ID
     * return
     */
    public static String getDeviceId() {
        return Paper.book().read("DeviceId", "000000000000000");
    }

}
