package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 21/03/2015.
 */
public class EventCategory extends AggregateRoot {

    private String deletedAt;
    private String avatarUrl;
    private String name;
    private String category;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }
}
