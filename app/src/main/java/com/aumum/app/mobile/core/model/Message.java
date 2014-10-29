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
    public static final int QUIT = 3;
    public static final int LIKE = 4;
    public static final int COMMENT = 5;
    public static final int REPLY_COMMENT = 6;
    public static final int DELETE_PARTY = 7;
    private static final String MESSAGE_BODY_OPTIONS[] = {
        "该消息已删除",
        "关注了您",
        "报名了亲子活动",
        "取消了报名亲子活动",
        "支持了亲子活动",
        "发表了评论",
        "回复了您的评论",
        "删除了亲子活动"
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
