package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 9/10/2014.
 */
public class SubscribeChannelEvent {
    private String channel;

    public String getChannel() {
        return channel;
    }

    public SubscribeChannelEvent(String channel) {
        this.channel = channel;
    }
}
