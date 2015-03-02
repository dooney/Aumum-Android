package com.aumum.app.mobile.core.dao.entity;

/**
 * Created by Administrator on 2/03/2015.
 */
public class MomentEntity extends BaseEntity {

    private String userId;
    private String details;
    private String images;
    private String likes;
    private String comments;

    public MomentEntity(String objectId,
                       java.util.Date createdAt,
                       String userId,
                       String details,
                       String images,
                       String likes,
                       String comments) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.userId = userId;
        this.details = details;
        this.images = images;
        this.likes = likes;
        this.comments = comments;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
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
}
