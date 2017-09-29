package com.sundy.iman.utils;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sundy on 17/9/14.
 */

public class CommonUtils {


    /**
     * 判断邮箱是否合法
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 图片取名
     *
     * @return
     */
    public static String formatImageName() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        int mSecond = calendar.get(Calendar.SECOND);
        String month = (mMonth >= 10) ? mMonth + "" : "0" + mMonth;
        String day = (mDay >= 10) ? mDay + "" : "0" + mDay;
        String hour = (mHour >= 10) ? mHour + "" : "0" + mHour;
        String minute = (mMinute >= 10) ? mMinute + "" : "0" + mMinute;
        String second = (mSecond >= 10) ? mSecond + "" : "0" + mSecond;
        return mYear + month + day + hour + minute + second;
    }

}
