package com.aumum.app.mobile.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 12/02/2015.
 */
public class UMengUtils {

    public static void init(Context context) {
        MobclickAgent.setDebugMode(false);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(context);
    }
}
