package com.sundy.iman.paperdb;

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

}
