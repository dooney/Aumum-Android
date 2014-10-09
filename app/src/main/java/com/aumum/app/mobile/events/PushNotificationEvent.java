package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 9/10/2014.
 */
public class PushNotificationEvent {
    private String channel;
    private String message;

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public PushNotificationEvent(String channel, String message) {
        this.channel = channel;
        this.message = message;
    }
}
