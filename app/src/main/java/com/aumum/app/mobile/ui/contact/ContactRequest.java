package com.aumum.app.mobile.ui.contact;

import com.aumum.app.mobile.core.model.User;

/**
 * Created by Administrator on 19/11/2014.
 */
public class ContactRequest {
    private User user;
    private String intro;

    public ContactRequest(User user, String intro) {
        this.user = user;
        this.intro = intro;
    }

    public User getUser() {
        return user;
    }

    public String getIntro() {
        return intro;
    }
}
