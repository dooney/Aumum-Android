package com.aumum.app.mobile.utils;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by Administrator on 25/12/2014.
 */
public class LocaleUtils {

    public static void init(Context context) {
        Locale locale = Locale.CHINA;
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
