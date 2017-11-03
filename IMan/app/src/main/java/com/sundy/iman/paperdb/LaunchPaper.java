package com.sundy.iman.paperdb;

import io.paperdb.Paper;

/**
 * App 第一次启动Paper记录类
 * Created by sundy on 17/9/21.
 */

public class LaunchPaper {
    private static final String PAPER_KEY = "Launch_App";

    /**
     * 保存第一次启动的记录
     */
    public static void saveLaunchRecord(boolean isLaunch) {
        Paper.book().write(PAPER_KEY, isLaunch);
    }

    /**
     * 清除第一次启动的记录
     */
    public static void deleteLaunchRecord() {
        Paper.book().delete(PAPER_KEY);
    }

    /**
     * 获取启动的记录
     * return
     * true: 第一次启动
     * false: 不是第一次启动
     */
    public static boolean getLaunchRecord() {
        return Paper.book().read(PAPER_KEY, true);
    }

}
