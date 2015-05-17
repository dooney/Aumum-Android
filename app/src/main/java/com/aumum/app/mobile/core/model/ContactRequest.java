package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 19/11/2014.
 */
public class ContactRequest {
    private String userId;
    private String info;
    private boolean isAdded;

    private UserInfo user;

    public ContactRequest(String userId,
                          String info) {
        this.userId = userId;
        this.info = info;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean isAdded) {
        this.isAdded = isAdded;
    }
}
