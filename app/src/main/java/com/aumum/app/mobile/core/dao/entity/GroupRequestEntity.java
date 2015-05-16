package com.aumum.app.mobile.core.dao.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table GROUP_REQUEST_ENTITY.
 */
public class GroupRequestEntity extends MessageEntity  {

    /** Not-null value. */
    private String groupId;
    /** Not-null value. */
    private String userId;
    private String info;
    private Integer status;

    public GroupRequestEntity() {
    }

    public GroupRequestEntity(String groupId) {
        this.groupId = groupId;
    }

    public GroupRequestEntity(String groupId, String userId, java.util.Date createdAt, String info, Integer status, Boolean isRead) {
        this.groupId = groupId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.info = info;
        this.status = status;
        this.isRead = isRead;
    }

    /** Not-null value. */
    public String getGroupId() {
        return groupId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /** Not-null value. */
    public String getUserId() {
        return userId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
