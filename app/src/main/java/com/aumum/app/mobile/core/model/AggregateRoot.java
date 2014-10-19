package com.aumum.app.mobile.core.model;

import com.aumum.app.mobile.utils.TimeUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;

/**
 * Created by Administrator on 27/09/2014.
 */
public abstract class AggregateRoot implements Serializable {
    protected String objectId;
    protected String createdAt;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(final String objectId) {
        this.objectId = objectId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAtFormatted() {
        DateTime time = new DateTime(createdAt, DateTimeZone.UTC);
        return TimeUtils.getFormattedTimeString(time);
    }
}
