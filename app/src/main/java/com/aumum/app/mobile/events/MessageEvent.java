package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 10/10/2014.
 */
public abstract class MessageEvent {
    private String toUserId;
    private String fromUserId;

    public MessageEvent(String toUserId, String fromUserId) {
        this.toUserId = toUserId;
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public abstract int getMessageType();
}
