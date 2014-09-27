package com.aumum.app.mobile.core;

import java.io.Serializable;

/**
 * Created by Administrator on 27/09/2014.
 */
public abstract class AggregateRoot implements Serializable {
    protected String objectId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(final String objectId) {
        this.objectId = objectId;
    }
}
