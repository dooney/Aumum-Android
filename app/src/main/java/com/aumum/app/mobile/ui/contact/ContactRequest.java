package com.aumum.app.mobile.ui.contact;

import com.aumum.app.mobile.core.model.User;

/**
 * Created by Administrator on 19/11/2014.
 */
public class ContactRequest {
    private User user;
    private String intro;
    private boolean isAdded;

    public ContactRequest(User user, String intro, boolean isAdded) {
        this.user = user;
        this.intro = intro;
        this.isAdded = isAdded;
    }

    public User getUser() {
        return user;
    }

    public String getIntro() {
        return intro;
    }

    public boolean isAdded() {
        return isAdded;
    }
}
