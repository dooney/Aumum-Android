package com.aumum.app.mobile.ui.contact;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Administrator on 19/11/2014.
 */
public class AddContactRequest implements Serializable {
    private String userId;
    private String userName;
    private String intro;

    public AddContactRequest() {

    }

    public AddContactRequest(String userId, String userName, String intro) {
        this.userId = userId;
        this.userName = userName;
        this.intro = intro;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getIntro() {
        return intro;
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
