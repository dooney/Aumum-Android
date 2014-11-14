package com.aumum.app.mobile.core.dao.entity;

/**
 * Created by Administrator on 13/11/2014.
 */
public class BaseEntity {
    /** Not-null value. */
    protected String instanceId;
    /** Not-null value. */
    protected String objectId;
    /** Not-null value. */
    protected java.util.Date createdAt;

    /** Not-null value. */
    public String getInstanceId() {
        return instanceId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    /** Not-null value. */
    public String getObjectId() {
        return objectId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /** Not-null value. */
    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }
}
