package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 9/10/2014.
 */
public class UnSubscribeChannelEvent {
    private String channel;

    public String getChannel() {
        return channel;
    }

    public UnSubscribeChannelEvent(String channel) {
        this.channel = channel;
    }
}
