package com.aumum.app.mobile.core.model;

import com.google.gson.Gson;

/**
 * Created by Administrator on 6/04/2015.
 */
public class GroupDescription {

    private String avatarUrl;
    private String description;

    public GroupDescription(String avatarUrl, String description) {
        this.avatarUrl = avatarUrl;
        this.description = description;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
