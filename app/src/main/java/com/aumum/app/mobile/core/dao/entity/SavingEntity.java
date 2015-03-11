package com.aumum.app.mobile.core.dao.entity;

/**
 * Created by Administrator on 12/03/2015.
 */
public class SavingEntity extends BaseEntity {

    private String userId;
    private double amount;
    private String images;
    private String likes;
    private String comments;

    public SavingEntity(String objectId,
                        java.util.Date createdAt,
                        String userId,
                        double amount,
                        String images,
                        String likes,
                        String comments) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.userId = userId;
        this.amount = amount;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
