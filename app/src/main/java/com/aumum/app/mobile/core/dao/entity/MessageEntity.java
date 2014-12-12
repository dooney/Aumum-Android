package com.aumum.app.mobile.core.dao.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

import java.util.Date;

/**
 * Entity mapped to table MESSAGE_VM.
 */
public class MessageEntity extends BaseEntity {

    public MessageEntity() {
    }

    public MessageEntity(String objectId) {
        this.objectId = objectId;
    }

    public MessageEntity(String context,
                         String objectId,
                         Date createdAt) {
        this.context = context;
        this.objectId = objectId;
        this.createdAt = createdAt;
    }
}
