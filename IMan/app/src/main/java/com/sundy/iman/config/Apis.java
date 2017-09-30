package com.sundy.iman.config;

import com.sundy.iman.BuildConfig;

/**
 * Api 地址常量
 * Created by sundy on 17/9/14.
 */

public class Apis {

    public static final String URL_BASE = BuildConfig.SERVER_URL;

    //发送验证码
    public static final String URL_SEND_VERIFICATION_CODE = "passport/sendVerificationCode";

    //登录
    public static final String URL_LOGIN = "passport/login";

    //获取个人用户信息
    public static final String URL_GET_MEMBER_INFO = "member/getMemberInfo";

    //登出
    public static final String URL_LOGOUT = "passport/logout";

    //更新支付密码接口
    public static final String URL_UPDATE_TRANSFER_PASSWORD = "member/updateTransferPassword";

    //修改语言接口
    public static final String URL_CHANGE_LANGUAGE = "member/changeLanguage";

    //保存个人信息接口
    public static final String URL_SAVE_MEMBER_INFO = "member/saveMemberInfo";



    //获取APP 版本
    public static final String URL_GET_APP_VERSION = "app/getVersion";

    //图片上传
    public static final String URL_UPLOAD_IMAGE = "";

    //获取标签列表
    public static final String URL_TAG_GET_LIST = "tag/getList";
}
