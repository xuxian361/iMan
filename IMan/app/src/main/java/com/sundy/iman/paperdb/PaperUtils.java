package com.sundy.iman.paperdb;

import android.text.TextUtils;

import com.sundy.iman.MainApp;
import com.sundy.iman.entity.AppVersionEntity;
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

    //获取登录用户ID
    public static String getMId() {
        if (isLogin())
            return getUserInfo().getData().getId();
        return "";
    }

    //获取登录用户Session Key
    public static String getSessionKey() {
        if (isLogin())
            return UserPaper.getSessionKey();
        return "";
    }

    //保存登录用户Session Key
    public static void setSessionKey(String sessionKey) {
        UserPaper.saveSessionKey(sessionKey);
    }

    //清除登录用户的本地信息
    public static void clearUserInfo() {
        UserPaper.deleteUserInfo();
    }

    //保存App 版本信息
    public static void setAppVersion(AppVersionEntity appVersion) {
        AppVersionPaper.saveAppVersion(appVersion);
    }

    //获取App 版本信息
    public static AppVersionEntity getAppVersion() {
        return AppVersionPaper.getAppVersion();
    }


}
