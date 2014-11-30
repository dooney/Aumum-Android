package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 10/10/2014.
 */
public class Comment extends AggregateRoot {
    private String parentId;
    private String repliedId;
    private String content;
    private String userId;

    private User user;

    public Comment(String parentId, String repliedId, String content, String userId) {
        this.parentId = parentId;
        this.repliedId = repliedId;
        this.content = content;
        this.userId = userId;
    }

    public String getParentId() {
        return parentId;
    }

    public String getRepliedId() {
        return repliedId;
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

    public boolean isOwner(String userId) {
        return userId.equals(this.userId);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
