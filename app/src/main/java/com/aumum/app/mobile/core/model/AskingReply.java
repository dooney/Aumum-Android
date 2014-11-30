package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 30/11/2014.
 */
public class AskingReply extends AggregateRoot {

    private String userId;
    private String content;

    private User user;

    public AskingReply(String userId,
                       String content) {
        this.userId = userId;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
