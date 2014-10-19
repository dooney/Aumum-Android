package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 10/10/2014.
 */
public class Comment extends AggregateRoot {
    private String parentId;
    private String text;
    private String userId;
    private User user;

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
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
