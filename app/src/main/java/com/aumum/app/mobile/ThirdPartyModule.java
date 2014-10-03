package com.aumum.app.mobile;

import android.content.Context;

import com.aumum.app.mobile.core.Constants;
import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Administrator on 3/10/2014.
 */
public final class ThirdPartyModule {

    public static void init(final Context context) {
        initParseCom(context);
    }

    private static void initParseCom(final Context context) {
        Parse.initialize(context, Constants.ParseCom.PARSE_APP_ID, Constants.ParseCom.PARSE_NOTIFICATION_API_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
