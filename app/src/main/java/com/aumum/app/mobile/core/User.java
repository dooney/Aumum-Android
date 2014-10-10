package com.aumum.app.mobile.core;

import java.util.ArrayList;
import java.util.List;

public class User extends AggregateRoot {
    protected String username;
    protected String sessionToken;
    protected Boolean emailVerified;
    protected int area;
    protected List<String> followers = new ArrayList<String>();
    protected List<String> followings = new ArrayList<String>();
    protected List<String> messages = new ArrayList<String>();
    protected List<String> parties = new ArrayList<String>();

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

    public List<String> getParties() {
        return parties;
    }
}
