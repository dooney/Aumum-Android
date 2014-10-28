package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 28/10/2014.
 */
public class PartyJoinReason extends AggregateRoot {
    private String content;
    private String userId;
    private User user;

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
}
