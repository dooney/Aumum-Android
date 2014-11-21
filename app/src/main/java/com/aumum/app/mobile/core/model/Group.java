package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 21/11/2014.
 */
public class Group {
    private String objectId;
    private String screenName;

    public Group(String objectId, String screenName) {
        this.objectId = objectId;
        this.screenName = screenName;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getScreenName() {
        return screenName;
    }
}
