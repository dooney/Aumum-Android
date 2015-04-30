package com.aumum.app.mobile.utils;

import android.content.Context;

import org.lasque.tusdk.core.TuSdk;

/**
 * Created by Administrator on 30/04/2015.
 */
public class TuSdkUtils {

    public static void init(Context context) {
        TuSdk.init(context, "003d2448e9edf965-00-2j9nn1");
    }
}
