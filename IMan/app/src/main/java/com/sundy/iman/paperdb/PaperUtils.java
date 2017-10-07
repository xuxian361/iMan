package com.sundy.iman.paperdb;

import android.text.TextUtils;

import com.sundy.iman.MainApp;
import com.sundy.iman.entity.MemberInfoEntity;
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

    //保存登录用户信息
    public static void saveUserInfo(MemberInfoEntity memberInfoEntity) {
        UserPaper.saveUserInfo(memberInfoEntity);
    }

    //获取登录用户信息
    public static MemberInfoEntity getUserInfo() {
        return UserPaper.getUserInfo();
    }

    //判断用户是否已登录
    public static boolean isLogin() {
        MemberInfoEntity memberInfoEntity = getUserInfo();
        if (memberInfoEntity != null) {
            MemberInfoEntity.DataEntity dataEntity = memberInfoEntity.getData();
            String userId = dataEntity.getId();
            if (!TextUtils.isEmpty(userId)) {
                return true;
            }
        }
        return false;
    }


}
