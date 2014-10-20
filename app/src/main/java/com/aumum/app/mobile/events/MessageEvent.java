package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 10/10/2014.
 */
public class MessageEvent {
    private int type;
    private String toUserId;
    private String fromUserId;

    public MessageEvent(int type, String toUserId, String fromUserId) {
        this.type = type;
        this.toUserId = toUserId;
        this.fromUserId = fromUserId;
    }

    public int getType() {
        return type;
    }

    public String getToUserId() {
        return toUserId;
    }

    public String getFromUserId() {
        return fromUserId;
    }
}
