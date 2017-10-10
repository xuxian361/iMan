package com.sundy.iman.paperdb;

import com.sundy.iman.entity.MemberInfoEntity;

import io.paperdb.Paper;

/**
 * 本地保存登录用户信息
 * Created by sundy on 17/9/14.
 */

public class UserPaper {

    private static final String PAPER_KEY = "UserPaper";
    private static final String SESSION_KEY = "session_key";

    /**
     * 保存用户信息
     */
    public static void saveUserInfo(MemberInfoEntity memberInfoEntity) {
        Paper.book().write(PAPER_KEY, memberInfoEntity);
    }

    /**
     * 清除登录用户的信息
     */
    public static void deleteUserInfo() {
        Paper.book().delete(PAPER_KEY);
        Paper.book().delete(SESSION_KEY);
    }

    /**
     * 获取登录用户信息
     */
    public static MemberInfoEntity getUserInfo() {
        return Paper.book().read(PAPER_KEY);
    }

    /**
     * 保存登录用户Session Key
     *
     * @param session_key
     */
    public static void saveSessionKey(String session_key) {
        Paper.book().write(SESSION_KEY, session_key);
    }

    /**
     * 获取登录用户Session Key
     *
     * @return
     */
    public static String getSessionKey() {
        return Paper.book().read(SESSION_KEY, "");
    }


}
