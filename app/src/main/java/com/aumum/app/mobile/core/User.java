package com.aumum.app.mobile.core;

import java.util.List;

public class User extends AggregateRoot {
    protected String username;
    protected String sessionToken;
    protected Boolean emailVerified;
    protected int area;
    protected List<String> followers;
    protected List<String> followings;
    protected List<String> messages;

    public String getUsername() {
        return username;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public int getArea() {
        return area;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public List<String> getFollowings() {
        return followings;
    }

    public List<String> getMessages() {
        return messages;
    }
}
