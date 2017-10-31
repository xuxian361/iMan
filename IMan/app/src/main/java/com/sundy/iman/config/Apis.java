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

    //获取七牛上传token接口
    public static final String URL_GET_QINIU_TOKEN = "upload/getQiniuToken";

    //创建社区
    public static final String URL_CREATE_COMMUNITY = "community/createCommunity";

    //社区列表
    public static final String URL_COMMUNITY_LIST = "community/getList";

    //加入或退出社区接口
    public static final String URL_JOIN_COMMUNITY = "community/joinCommunity";

    //获取标签列表
    public static final String URL_TAG_GET_LIST = "tag/getList";

    //创建post接口
    public static final String URL_CREATE_POST = "post/createPost";

    //post列表接口
    public static final String URL_POST_LIST = "post/getList";

    //创建广告接口
    public static final String URL_CREATE_ADVERTISING = "post/createAdvertising";

    //删除post接口
    public static final String URL_DELETE_POST = "post/deletePost";

    //取消post接口
    public static final String URL_CANCEL_POST = "post/cancelPost";

    //更新post接口
    public static final String URL_UPDATE_POST = "post/updatePost";

    //获取post信息接口
    public static final String URL_GET_POST_INFO = "post/getInfo";

    //领取广告奖励接口
    public static final String URL_COLLECT_ADVERTISING = "post/collectAdvertising";

    //加入推广社区接口
    public static final String URL_JOIN_PROMOTE_COMMUNITY = "promoteCommunity/joinPromote";

    //加入推广社区接口
    public static final String URL_QUIT_PROMOTE_COMMUNITY = "promoteCommunity/quitPromote";

    //举报post接口
    public static final String URL_REPORT_POST = "post/reportPost";

    //我的推广社区列表接口
    public static final String URL_GET_MY_PROMOTE_COMMUNITY_LIST = "promoteCommunity/getMyList";

    //社区详情接口
    public static final String URL_GET_COMMUNITY_INFO = "community/getInfo";

    //首页列表接口
    public static final String URL_HOME_GET_LIST = "home/getList";

    //更新版本
    public static final String URL_UPDATE_VERSION = "versionInfo/updateVersion";

    //获取静态内容接口
    public static final String URL_GET_STATIC_CONTENT = "staticContent/getStaticContent";

    //解析url（二维码值）
    public static final String URL_PARSE_URL = "url/parse";

    //获取分享内容
    public static final String URL_GET_SHARE_INFO = "share/getShareInfo";

    //图片上传
    public static final String URL_UPLOAD_IMAGE = "";


}
