package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.utils.Ln;
import com.parse.ParsePush;

/**
 * Created by Administrator on 18/11/2014.
 */
public class NotificationService {

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

    public void pushNotification(String channel, String content) {
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
