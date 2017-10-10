package com.sundy.iman.paperdb;

import com.sundy.iman.entity.AppVersionEntity;

import io.paperdb.Paper;

/**
 * 本地保存App版本信息
 * Created by sundy on 17/10/10.
 */

public class AppVersionPaper {

    private static final String PAPER_KEY = "AppVersionPaper";

    /**
     * 保存版本信息
     */
    public static void saveAppVersion(AppVersionEntity appVersionEntity) {
        Paper.book().write(PAPER_KEY, appVersionEntity);
    }

    /**
     * 获取版本信息
     *
     * @return
     */
    public static AppVersionEntity getAppVersion() {
        return Paper.book().read(PAPER_KEY);
    }


}
