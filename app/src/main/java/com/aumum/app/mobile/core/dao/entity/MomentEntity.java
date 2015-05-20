package com.aumum.app.mobile.core.dao.entity;

/**
 * Created by Administrator on 2/03/2015.
 */
public class MomentEntity extends BaseEntity {

    private String userId;
    private String likes;
    private String text;
    private String imageUrl;
    private Integer hot;

    public MomentEntity(String objectId,
                       java.util.Date createdAt,
                       String userId,
                       String likes,
                       String text,
                       String imageUrl,
                       Integer hot) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.userId = userId;
        this.likes = likes;
        this.text = text;
        this.imageUrl = imageUrl;
        this.hot = hot;
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

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }
}
