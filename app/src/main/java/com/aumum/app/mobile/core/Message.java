package com.aumum.app.mobile.core;

/**
 * Created by Administrator on 7/10/2014.
 */
public class Message extends AggregateRoot {
    private String fromUserId;
    private int type;
    private User fromUser;

    public static final int DELETED = 0;
    public static final int FOLLOW = 1;
    private static final String MESSAGE_BODY_OPTIONS[] = {
        "该消息已删除",
        "关注了你"
    };

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getFromUser() {
        return fromUser;
    }

    public String getBody() {
        return MESSAGE_BODY_OPTIONS[type];
    }
}
