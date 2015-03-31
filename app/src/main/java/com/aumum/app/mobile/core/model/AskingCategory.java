package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 31/03/2015.
 */
public class AskingCategory extends AggregateRoot {

    private String deletedAt;
    private String avatarUrl;
    private String screenName;
    private String description;
    private int category;
    private int seq;
    private boolean unread;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getDescription() {
        return description;
    }

    public int getCategory() {
        return category;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }
}
