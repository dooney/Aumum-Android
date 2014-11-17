package com.aumum.app.mobile.utils;

import android.content.Context;

import com.aumum.app.mobile.core.Constants;
import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Administrator on 8/10/2014.
 */
public class NotificationUtils {

    public static void init(Context context) {
        Parse.initialize(context, Constants.Http.PARSE_APP_ID, Constants.Http.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
