package com.sundy.iman.paperdb;

import android.text.TextUtils;

import com.sundy.iman.MainApp;
import com.sundy.iman.entity.AppVersionEntity;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.helper.LocaleHelper;
import com.sundy.iman.utils.DeviceUtils;

import java.util.List;

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
        LocaleHelper.setLocale(MainApp.getInstance(), language);
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

    //获取Post 列表中已读的IDs
    public static List<String> getPostReadIds(String community_id) {
        return PostListInReadPaper.getPostReadIds(community_id);
    }

    //获取Post 是否已读
    public static boolean isPostRead(String community_id, String post_id) {
        boolean isRead = false;
        List<String> readIds = getPostReadIds(community_id);
        if (readIds != null && readIds.size() > 0) {
            if (readIds.contains(post_id)) {
                isRead = true;
            }
        }
        return isRead;
    }

    //保存Post 列表中已读的ID
    public static void savePostReadID(String community_id, String post_id) {
        PostListInReadPaper.saveReadId(community_id, post_id);
    }

    //清除所有本地保存的post 已读消息记录
    public static void clearPostReadRecord() {
        PostListInReadPaper.deleteAllPostReadIds();
    }

}
