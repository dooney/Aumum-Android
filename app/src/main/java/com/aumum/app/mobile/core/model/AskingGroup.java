package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 31/03/2015.
 */
public class AskingGroup extends AggregateRoot {

    private String deletedAt;
    private String avatarUrl;
    private String screenName;
    private String description;
    private int seq;
    private boolean unread;
    private boolean isMember;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean isMember) {
        this.isMember = isMember;
    }
}
