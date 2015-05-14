package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 3/03/2015.
 */
public class Comment extends AggregateRoot {

    private String deletedAt;
    private String userId;
    private String content;
    private String parentId;

    private UserInfo user;
    private Boolean isOwner;

    public Comment(String userId,
                   String content,
                   String parentId) {
        this.userId = userId;
        this.content = content;
        this.parentId = parentId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(String userId) {
        isOwner = userId.equals(this.userId);
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getReplyPrefix() {
        if (user != null) {
            return "@" + user.getScreenName() + " : ";
        }
        return "";
    }
}
