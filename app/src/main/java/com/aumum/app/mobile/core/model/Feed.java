package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 15/03/2015.
 */
public class Feed extends AggregateRoot {

    private String screenName;
    private String avatarUrl;
    private String uri;

    public String getScreenName() {
        return screenName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getUri() {
        return uri;
    }
}