package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 19/11/2014.
 */
public class ContactRequest {
    private UserInfo user;
    private String info;
    private boolean isAdded;
    private String createdAt;

    public ContactRequest(UserInfo user,
                          String info,
                          boolean isAdded) {
        this.user = user;
        this.info = info;
        this.isAdded = isAdded;
    }

    public UserInfo getUser() {
        return user;
    }

    public String getInfo() {
        return info;
    }

    public boolean isAdded() {
        return isAdded;
    }
}
