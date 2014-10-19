package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 7/10/2014.
 */
public class Message extends AggregateRoot {
    private String fromUserId;
    private String toUserId;
    private int type;
    private User fromUser;

    public static final int DELETED = 0;
    public static final int FOLLOW = 1;
    public static final int JOIN = 2;
    public static final int UNJOIN = 3;
    public static final int LIKE = 4;
    private static final String MESSAGE_BODY_OPTIONS[] = {
        "该消息已删除",
        "关注了你",
        "报名了亲子活动",
        "取消了报名亲子活动",
        "支持了亲子活动",
    };

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
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
