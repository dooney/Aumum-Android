package com.aumum.app.mobile.core.model;

import com.aumum.app.mobile.core.dao.vm.UserVM;

/**
 * Created by Administrator on 10/10/2014.
 */
public class Comment extends AggregateRoot {
    private String parentId;
    private String repliedId;
    private String content;
    private String userId;
    private UserVM user;

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setRepliedId(String repliedId) {
        this.repliedId = repliedId;
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

    public UserVM getUser() {
        return user;
    }

    public void setUser(UserVM user) {
        this.user = user;
    }

    public boolean isOwner(String userId) {
        return userId.equals(this.userId);
    }
}
