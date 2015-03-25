package com.aumum.app.mobile.core.dao.entity;

/**
 * Created by Administrator on 25/03/2015.
 */
public class GroupRequestEntity {

    private Long id;
    /** Not-null value. */
    private String groupId;
    private String userId;
    private String reason;
    private int status;

    public GroupRequestEntity() {
    }

    public GroupRequestEntity(Long id) {
        this.id = id;
    }

    public GroupRequestEntity(Long id,
                              String groupId,
                              String userId,
                              String reason,
                              int status) {
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
        this.reason = reason;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getGroupId() {
        return groupId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
