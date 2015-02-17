package com.aumum.app.mobile.utils;

import android.content.Context;

import com.aumum.app.mobile.core.Constants;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

/**
 * Created by Administrator on 17/02/2015.
 */
public class PushUtils {

    public static void init(Context context) {
        Parse.initialize(context, Constants.Http.PARSE_APP_ID, Constants.Http.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("Admin");
    }
}
