package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 3/03/2015.
 */
public class Comment extends AggregateRoot {

    private String deletedAt;
    private String parentId;
    private String repliedId;
    private String content;
    private String userId;
    private List<String> likes = new ArrayList<String>();

    private User user;

    public Comment(String parentId,
                   String repliedId,
                   String content,
                   String userId) {
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

    public int getLikesCount() {
        if (likes != null) {
            return likes.size();
        }
        return 0;
    }

    public boolean isLiked(String userId) {
        if (likes != null) {
            return likes.contains(userId);
        }
        return false;
    }
}
