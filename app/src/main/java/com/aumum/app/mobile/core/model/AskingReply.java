package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 30/11/2014.
 */
public class AskingReply extends AggregateRoot {

    private String deletedAt;
    private String userId;
    private String content;
    private String repliedId;
    private Boolean isAnonymous;
    private List<String> likes = new ArrayList<String>();

    private User user;

    public AskingReply(String userId,
                       String content,
                       String repliedId,
                       Boolean isAnonymous) {
        this.userId = userId;
        this.content = content;
        this.repliedId = repliedId;
        this.isAnonymous = isAnonymous;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public String getRepliedId() {
        return repliedId;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous != null ? isAnonymous : false;
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
