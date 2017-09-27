package com.sundy.iman.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * Created by sundy on 17/9/14.
 */

public class DateUtils {

    /**
     * 获取时间戳
     *
     * @return
     */
    public static String getTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        int mSecond = calendar.get(Calendar.SECOND);
        String m = (mMonth >= 10) ? mMonth + "" : "0" + mMonth;
        String d = (mDay >= 10) ? mDay + "" : "0" + mDay;
        String hour = (mHour >= 10) ? mHour + "" : "0" + mHour;
        String minute = (mMinute >= 10) ? mMinute + "" : "0" + mMinute;
        String second = (mSecond >= 10) ? mSecond + "" : "0" + mSecond;
        return mYear + m + d + hour + minute + second;
    }

    /**
     * 日期格式化
     *
     * @param oldFormat
     * @param newFormat
     * @param dateStr
     * @return
     */
    public static String formatDate(String oldFormat, String newFormat, String dateStr) {
        String result = dateStr;
        try {
            SimpleDateFormat sdf_old = new SimpleDateFormat(oldFormat);
            SimpleDateFormat sdf_new = new SimpleDateFormat(newFormat);
            Date date = sdf_old.parse(dateStr);
            result = sdf_new.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取两个日期的天数
     * date2比date1多的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getIntervalDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) //同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) //闰年
                {
                    timeDistance += 366;
                } else //不是闰年
                {
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else //不同年
        {
            System.out.println("判断day2 - day1 : " + (day2 - day1));
            return day2 - day1;
        }
    }

    /**
     * 对比两个时间
     *
     * @param date1 大
     * @param date2 小
     * @return
     */
    public static int compareDate(Date date1, Date date2) {
        try {
            if (date1.getTime() > date2.getTime()) {
                return 1;
            } else if (date1.getTime() < date2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
