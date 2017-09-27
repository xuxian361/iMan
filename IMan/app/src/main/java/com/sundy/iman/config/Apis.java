package com.sundy.iman.config;

import com.sundy.iman.BuildConfig;

/**
 * Api 地址常量
 * Created by sundy on 17/9/14.
 */

public class Apis {

    public static final String URL_BASE = BuildConfig.SERVER_URL;

    //获取APP 版本
    public static final String URL_GET_APP_VERSION = "app/getVersion";

    //图片上传
    public static final String URL_UPLOAD_IMAGE = "";

    //获取标签列表
    public static final String URL_TAG_GET_LIST = "tag/getList";
}
