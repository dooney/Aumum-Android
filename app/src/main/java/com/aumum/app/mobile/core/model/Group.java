package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 10/11/2014.
 */
public class Group {
    private String objectId;
    private String screenName;
    private int currentSize;
    private boolean isMember;
    private boolean isMembersOnly;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(int currentSize) {
        this.currentSize = currentSize;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean isMember) {
        this.isMember = isMember;
    }

    public boolean isMembersOnly() {
        return isMembersOnly;
    }

    public void setMembersOnly(boolean isMembersOnly) {
        this.isMembersOnly = isMembersOnly;
    }
}
