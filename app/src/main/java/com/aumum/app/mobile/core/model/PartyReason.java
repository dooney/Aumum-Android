package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 28/10/2014.
 */
public class PartyReason extends AggregateRoot {
    private int type;
    private String content;
    private String userId;
    private User user;

    public static final int JOIN = 0;
    public static final int QUIT = 1;
    public static final String ACTION_OPTIONS[] = {
        "报名了该活动",
        "退出了该活动"
    };

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getActionText() {
        return ACTION_OPTIONS[type];
    }
}
