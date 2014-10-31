package com.aumum.app.mobile.utils;

import android.content.Context;

import com.aumum.app.mobile.core.Constants;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

/**
 * Created by Administrator on 8/10/2014.
 */
public class NotificationUtils {

    public static void init(Context context) {
        Parse.initialize(context, Constants.Http.PARSE_APP_ID, Constants.Http.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static void subscribe(String channel) {
        if (channel != null) {
            try {
                ParsePush.subscribeInBackground(channel);
            } catch (Exception e) {
                Ln.d(e);
            }
        }
    }

    public static void unSubscribe(String channel) {
        if (channel != null) {
            try {
                ParsePush.unsubscribeInBackground(channel);
            } catch (Exception e) {
                Ln.d(e);
            }
        }
    }

    public static void pushNotification(String channel, String content) {
        if (channel != null) {
            try {
                ParsePush push = new ParsePush();
                push.setChannel(channel);
                push.setMessage(content);
                push.sendInBackground();
            } catch (Exception e) {
                Ln.d(e);
            }
        }
    }
}
