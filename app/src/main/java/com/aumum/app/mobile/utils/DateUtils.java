package com.aumum.app.mobile.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 13/11/2014.
 */
public class DateUtils {

    public static Date stringToDate(String str, String format) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.parse(str);
    }

    public static String dateToString(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }
}
