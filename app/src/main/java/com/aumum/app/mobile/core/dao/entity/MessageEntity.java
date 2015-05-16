package com.aumum.app.mobile.core.dao.entity;

/**
 * Created by Administrator on 16/05/2015.
 */
public class MessageEntity {

    /** Not-null value. */
    protected java.util.Date createdAt;

    protected Boolean isRead;

    /** Not-null value. */
    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
