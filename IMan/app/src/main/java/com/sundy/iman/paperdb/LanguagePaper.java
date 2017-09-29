package com.sundy.iman.paperdb;

import io.paperdb.Paper;

/**
 * APP 语言管理 Paper
 * Created by sundy on 17/9/26.
 */

public class LanguagePaper {

    private static final String PAPER_KEY = "LanguagePaper";

    /**
     * 保存App 语言
     *
     * @param language
     */
    public static void saveLanguage(String language) {
        Paper.book().write(PAPER_KEY, language);
    }

    /**
     * 获取语言
     *
     * @return
     */
    public static String getLanguage() {
        return Paper.book().read(PAPER_KEY, "en"); //en-英文，sc-简体中文，tc-繁体中文
    }

}
