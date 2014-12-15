package com.aumum.app.mobile.utils;

import android.content.Context;

import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 16/12/2014.
 */
public class SmsSdkUtils {

    private static final String APP_ID = "4b3aee0612e8";
    private static final String APP_CLIENT = "f6477ba59f492d8d04ca97378236da46";

    public static void init(Context context) {
        SMSSDK.initSDK(context, APP_ID, APP_CLIENT);
    }
}
