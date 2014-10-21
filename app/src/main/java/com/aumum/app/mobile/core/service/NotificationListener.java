package com.aumum.app.mobile.core.service;

import android.content.Context;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.utils.Ln;
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
            try {
                ParsePush.subscribeInBackground(channel);
            } catch (Exception e) {
                Ln.d(e);
            }
        }
    }

    public void unSubscribe(String channel) {
        if (channel != null) {
            try {
                ParsePush.unsubscribeInBackground(channel);
            } catch (Exception e) {
                Ln.d(e);
            }
        }
    }

    public void pushNotification(String channel) {
        if (channel != null) {
            try {
                ParsePush push = new ParsePush();
                push.setChannel(channel);
                push.setMessage(NOTIFICATION_TEXT);
                push.sendInBackground();
            } catch (Exception e) {
                Ln.d(e);
            }
        }
    }
}
