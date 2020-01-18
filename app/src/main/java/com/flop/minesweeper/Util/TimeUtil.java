package com.flop.minesweeper.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * Created by Flop on 2018/11/20.
 */
public class TimeUtil {
    /**
     * 时间转化为星期
     *
     * @param indexOfWeek 星期的第几天
     */
    private static String getWeekDayStr(int indexOfWeek) {
        String weekDayStr = "";
        switch (indexOfWeek) {
            case 1:
                weekDayStr = "星期日";
                break;
            case 2:
                weekDayStr = "星期一";
                break;
            case 3:
                weekDayStr = "星期二";
                break;
            case 4:
                weekDayStr = "星期三";
                break;
            case 5:
                weekDayStr = "星期四";
                break;
            case 6:
                weekDayStr = "星期五";
                break;
            case 7:
                weekDayStr = "星期六";
                break;
        }
        return weekDayStr;
    }

    /**
     * 获取日期时间戳
     *
     * @param time    需要格式化的时间 如"2014-07-14 19:01:45"
     * @param pattern 输入参数time的时间格式 如:"yyyy-MM-dd HH:mm:ss"
     *                如果为空则默认使用"yyyy-MM-dd HH:mm:ss"格式
     * @return time为null，或者时间格式不匹配，输出空字符""
     * @throws ParseException
     */
    public static long timeStamp(String time, String pattern) {
        long dTime = 0;
        if (time != null) {
            if (pattern == null)
                pattern = "yyyy-MM-dd HH:mm:ss";
            Date tDate;
            try {
                tDate = new SimpleDateFormat(pattern).parse(time);
                if (tDate != null) {
                    dTime = tDate.getTime() / 1000;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dTime;
    }

    /**
     * 时间转换
     */
    public static String dateFormat(String date) {
        long timeStamp = timeStamp(date, "yyyy-MM-dd HH:mm");
        if (timeStamp == 0)
            return "";
        Calendar inputTime = Calendar.getInstance();
        String timeStr = timeStamp + "";
        if (timeStr.length() == 10) {
            timeStamp = timeStamp * 1000;
        }
        inputTime.setTimeInMillis(timeStamp);
        Date currentTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);//控制时
        calendar.set(Calendar.MINUTE, 0);//控制分
        calendar.set(Calendar.SECOND, 0);//控制秒
        calendar.set(Calendar.MILLISECOND, 0);//控制毫秒
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(currentTimeZone);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            return "昨天";
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, -5);
            if (calendar.before(inputTime)) {
                return getWeekDayStr(inputTime.get(Calendar.DAY_OF_WEEK));
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + "-" + "M" + "-" + "d" + " ");
                return sdf.format(currentTimeZone);
            }
        }
    }

    /**
     * 获取当前时间
     */
    public static String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
}
