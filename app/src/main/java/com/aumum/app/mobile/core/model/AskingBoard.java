package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 2/04/2015.
 */
public class AskingBoard extends AggregateRoot {

    private String deletedAt;
    private String avatarUrl;
    private String screenName;
    private String description;
    private int seq;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getDescription() {
        return description;
    }
}
