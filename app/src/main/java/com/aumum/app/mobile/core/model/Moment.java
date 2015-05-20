package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2/03/2015.
 */
public class Moment extends AggregateRoot implements RefreshItem {

    private String deletedAt;
    private String userId;
    private List<String> likes = new ArrayList<String>();
    private String text;
    private String imageUrl;
    private Integer hot;

    private UserInfo user;
    private Boolean isOwner;
    private Boolean isLiked;
    private List<UserInfo> likesInfo;

    public Moment(String userId,
                  String text,
                  String imageUrl) {
        this.userId = userId;
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public Moment(String objectId,
                  String createdAt,
                  String userId,
                  List<String> likes,
                  String text,
                  String imageUrl) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.userId = userId;
        if (likes != null) {
            this.likes.clear();
            this.likes.addAll(likes);
        }
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public String getUserId() {
        return userId;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public List<String> getLikes() {
        return likes;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getHot() {
        return hot;
    }

    public void setOwner(String userId) {
         isOwner = userId.equals(this.userId);
    }

    public boolean isOwner() {
        return isOwner;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(String userId) {
        if (likes != null) {
            isLiked = likes.contains(userId);
        } else {
            isLiked = false;
        }
    }

    public void setLikesInfo(List<UserInfo> users) {
        if (likesInfo == null) {
            likesInfo = new ArrayList<>();
        }
        likesInfo.clear();
        likesInfo.addAll(users);
    }

    public List<UserInfo> getLikesInfo() {
        return likesInfo;
    }

    public void addLike(String userId) {
        if (likes != null && !likes.contains(userId)) {
            likes.add(userId);
        }
    }

    public void removeLike(String userId) {
        if (likes != null && likes.contains(userId)) {
            likes.remove(userId);
        }
    }

    public void addLikeInfo(UserInfo user) {
        if (likesInfo == null) {
            likesInfo = new ArrayList<>();
        }
        likesInfo.add(user);
    }

    public void removeLikeInfo(String userId) {
        if (likesInfo != null) {
            for (Iterator<UserInfo> it = likesInfo.iterator(); it.hasNext();) {
                UserInfo user = it.next();
                if (user.getObjectId().equals(userId)) {
                    it.remove();
                    break;
                }
            }
        }
    }
}