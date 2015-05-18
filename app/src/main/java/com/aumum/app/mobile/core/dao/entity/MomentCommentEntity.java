package com.aumum.app.mobile.core.dao.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table MOMENT_COMMENT_ENTITY.
 */
public class MomentCommentEntity extends MessageEntity  {

    private Long id;
    /** Not-null value. */
    private String userId;
    /** Not-null value. */
    private String momentId;
    private String content;

    public MomentCommentEntity() {
    }

    public MomentCommentEntity(Long id) {
        this.id = id;
    }

    public MomentCommentEntity(Long id, String userId, java.util.Date createdAt, String momentId, String content, Boolean isRead) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.momentId = momentId;
        this.content = content;
        this.isRead = isRead;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getUserId() {
        return userId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** Not-null value. */
    public String getMomentId() {
        return momentId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}