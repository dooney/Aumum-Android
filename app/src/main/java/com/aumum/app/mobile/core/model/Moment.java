package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 8/11/2014.
 */
public class Moment extends AggregateRoot {
    private String text;
    private String userId;
    private User user;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
