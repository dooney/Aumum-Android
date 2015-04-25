package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 19/11/2014.
 */
public class ContactRequest {
    private UserInfo user;
    private String intro;
    private boolean isAdded;

    public ContactRequest(UserInfo user, String intro, boolean isAdded) {
        this.user = user;
        this.intro = intro;
        this.isAdded = isAdded;
    }

    public UserInfo getUser() {
        return user;
    }

    public String getIntro() {
        return intro;
    }

    public boolean isAdded() {
        return isAdded;
    }
}
