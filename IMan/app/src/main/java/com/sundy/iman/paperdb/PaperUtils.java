package com.sundy.iman.paperdb;

import com.sundy.iman.MainApp;
import com.sundy.iman.utils.DeviceUtils;

/**
 * Paper DB 工具类
 * Created by sundy on 17/9/21.
 */

public class PaperUtils {

    //判断是否第一次启动APP
    public static boolean isFirstLaunchApp() {
        return LaunchPaper.getLaunchRecord();
    }

    //设置第一次启动APP标志
    public static void setFirstLaunchApp() {
        LaunchPaper.saveLaunchRecord(true);
    }

    //获取App 当前语言
    public static String getLanguage() {
        return LanguagePaper.getLanguage();
    }

    //保存App 当前语言
    public static void setLanguage(String language) {
        LanguagePaper.saveLanguage(language);
    }

    //保存设备ID
    public static void setDeviceId() {
        CommonDataPaper.saveDeviceId(DeviceUtils.getDeviceIMEI(MainApp.getInstance()));
    }

    //获取设备ID
    public static String getDeviceId() {
        return CommonDataPaper.getDeviceId();
    }


}
