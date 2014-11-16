package com.aumum.app.mobile.core.dao.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

import java.util.Date;

/**
 * Entity mapped to table MESSAGE_VM.
 */
public class MessageEntity extends BaseEntity {

    private String fromUserId;
    private String toUserId;
    private Integer type;
    private String content;
    private String parent;

    public MessageEntity() {
    }

    public MessageEntity(String objectId) {
        this.objectId = objectId;
    }

    public MessageEntity(String objectId,
                         Date createdAt,
                         String fromUserId,
                         String toUserId,
                         Integer type,
                         String content,
                         String parent) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.type = type;
        this.content = content;
        this.parent = parent;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
