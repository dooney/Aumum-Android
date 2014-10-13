package com.aumum.app.mobile.core;

/**
 * Created by Administrator on 10/10/2014.
 */
public class Comment extends AggregateRoot {
    private String text;
    private String userId;
    private User user;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
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
