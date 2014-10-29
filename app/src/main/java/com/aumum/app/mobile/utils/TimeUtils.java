package com.aumum.app.mobile.utils;

import org.joda.time.DateTime;

import java.util.Calendar;

/**
 * Created by Administrator on 19/10/2014.
 */
public class TimeUtils {
    private static int MILL_MIN = 1000 * 60;
    private static int MILL_HOUR = MILL_MIN * 60;
    private static int MILL_DAY = MILL_HOUR * 24;

    private static String JUST_NOW = "刚刚";
    private static String MIN = "分钟前";
    private static String HOUR = "小时前";
    private static String DAY = "天前";
    private static String MONTH = "月前";
    private static String YEAR = "年前";

    private static String YESTERDAY = "昨天";
    private static String THE_DAY_BEFORE_YESTERDAY = "前天";
    private static String TODAY = "今天";

    private static String DATE_FORMAT = "M月d日 HH:mm";
    private static String YEAR_FORMAT = "yyyy年 M月d日 HH:mm";

    private static Calendar msgCalendar = null;
    private static java.text.SimpleDateFormat dayFormat = null;
    private static java.text.SimpleDateFormat dateFormat = null;
    private static java.text.SimpleDateFormat yearFormat = null;

    public static String getFormattedTimeString(DateTime time) {
        long now = System.currentTimeMillis();
        long millis = time.getMillis();

        Calendar nowCalendar = Calendar.getInstance();

        if (msgCalendar == null)
            msgCalendar = Calendar.getInstance();

        msgCalendar.setTimeInMillis(millis);

        long calcMills = now - millis;

        long calSeconds = calcMills / 1000;

        if (calSeconds < 60) {
            return JUST_NOW;
        }

        long calMins = calSeconds / 60;

        if (calMins < 60) {

            return new StringBuilder().append(calMins).append(MIN).toString();
        }

        long calHours = calMins / 60;

        if (calHours < 24 && isSameDay(nowCalendar, msgCalendar)) {
            if (dayFormat == null)
                dayFormat = new java.text.SimpleDateFormat("HH:mm");

            String result = dayFormat.format(msgCalendar.getTime());
            return new StringBuilder().append(TODAY).append(" ").append(result).toString();

        }

        long calDay = calHours / 24;

        if (calDay < 31) {
            if (isYesterday(nowCalendar, msgCalendar)) {
                if (dayFormat == null)
                    dayFormat = new java.text.SimpleDateFormat("HH:mm");

                String result = dayFormat.format(msgCalendar.getTime());
                return new StringBuilder(YESTERDAY).append(" ").append(result).toString();

            } else if (isTheDayBeforeYesterday(nowCalendar, msgCalendar)) {
                if (dayFormat == null)
                    dayFormat = new java.text.SimpleDateFormat("HH:mm");

                String result = dayFormat.format(msgCalendar.getTime());
                return new StringBuilder(THE_DAY_BEFORE_YESTERDAY).append(" ").append(result).toString();

            } else {
                if (dateFormat == null)
                    dateFormat = new java.text.SimpleDateFormat(DATE_FORMAT);

                String result = dateFormat.format(msgCalendar.getTime());
                return new StringBuilder(result).toString();
            }
        }

        long calMonth = calDay / 31;

        if (calMonth < 12 && isSameYear(nowCalendar, msgCalendar)) {
            if (dateFormat == null)
                dateFormat = new java.text.SimpleDateFormat(DATE_FORMAT);

            String result = dateFormat.format(msgCalendar.getTime());
            return new StringBuilder().append(result).toString();

        }
        if (yearFormat == null)
            yearFormat = new java.text.SimpleDateFormat(YEAR_FORMAT);
        String result = yearFormat.format(msgCalendar.getTime());
        return new StringBuilder().append(result).toString();
    }

    private static boolean isSameHalfDay(Calendar now, Calendar msg) {
        int nowHour = now.get(Calendar.HOUR_OF_DAY);
        int msgHOur = msg.get(Calendar.HOUR_OF_DAY);

        if (nowHour <= 12 & msgHOur <= 12) {
            return true;
        } else if (nowHour >= 12 & msgHOur >= 12) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isSameDay(Calendar now, Calendar msg) {
        int nowDay = now.get(Calendar.DAY_OF_YEAR);
        int msgDay = msg.get(Calendar.DAY_OF_YEAR);

        return nowDay == msgDay;
    }

    private static boolean isYesterday(Calendar now, Calendar msg) {
        int nowDay = now.get(Calendar.DAY_OF_YEAR);
        int msgDay = msg.get(Calendar.DAY_OF_YEAR);

        return (nowDay - msgDay) == 1;
    }

    private static boolean isTheDayBeforeYesterday(Calendar now, Calendar msg) {
        int nowDay = now.get(Calendar.DAY_OF_YEAR);
        int msgDay = msg.get(Calendar.DAY_OF_YEAR);

        return (nowDay - msgDay) == 2;
    }

    private static boolean isSameYear(Calendar now, Calendar msg) {
        int nowYear = now.get(Calendar.YEAR);
        int msgYear = msg.get(Calendar.YEAR);

        return nowYear == msgYear;
    }
}
