package com.aumum.app.mobile.core.model;

import java.io.Serializable;

/**
 * Created by Administrator on 31/10/2014.
 */
public class MessageParent implements Serializable {
    private String objectId;
    private String content;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
