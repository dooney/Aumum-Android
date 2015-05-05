package com.aumum.app.mobile.core.dao.entity;

/**
 * Created by Administrator on 2/03/2015.
 */
public class MomentEntity extends BaseEntity {

    private String userId;
    private String likes;
    private String comments;
    private String text;
    private String imageUrl;

    public MomentEntity(String objectId,
                       java.util.Date createdAt,
                       String userId,
                       String likes,
                       String comments,
                       String text,
                       String imageUrl) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.userId = userId;
        this.likes = likes;
        this.comments = comments;
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
