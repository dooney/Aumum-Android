package com.aumum.app.mobile.core.service;

import android.content.Context;

import com.aumum.app.mobile.core.Constants;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

/**
 * Created by Administrator on 8/10/2014.
 */
public class NotificationListener {

    private final String NOTIFICATION_TEXT = "您有新的消息";

    public NotificationListener(Context context) {
        Parse.initialize(context, Constants.Http.PARSE_APP_ID, Constants.Http.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public void subscribe(String channel) {
        if (channel != null) {
            ParsePush.subscribeInBackground(channel);
        }
    }

    public void unSubscribe(String channel) {
        if (channel != null) {
            ParsePush.unsubscribeInBackground(channel);
        }
    }

    public void pushNotification(String channel) {
        if (channel != null) {
            ParsePush push = new ParsePush();
            push.setChannel(channel);
            push.setMessage(NOTIFICATION_TEXT);
            push.sendInBackground();
        }
    }
}
