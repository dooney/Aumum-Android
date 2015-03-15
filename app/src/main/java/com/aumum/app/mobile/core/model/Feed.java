package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 15/03/2015.
 */
public class Feed extends AggregateRoot {

    private String deletedAt;
    private int seq;
    private String screenName;
    private String avatarUrl;
    private String uri;
    private String description;

    public String getScreenName() {
        return screenName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getUri() {
        return uri;
    }

    public String getDescription() {
        return description;
    }
}
