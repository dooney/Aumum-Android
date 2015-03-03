package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2/03/2015.
 */
public class Moment extends AggregateRoot {

    private String deletedAt;
    private String userId;
    private String details;
    private List<String> images = new ArrayList<String>();
    private List<String> likes = new ArrayList<String>();
    private List<String> comments = new ArrayList<String>();

    private User user;

    public Moment(String userId,
                  String details,
                  List<String> images) {
        this.userId = userId;
        this.details = details;
        if (images != null) {
            this.images.clear();
            this.images.addAll(images);
        }
    }

    public Moment(String objectId,
                  String createdAt,
                  String userId,
                  String details,
                  List<String> images,
                  List<String> likes,
                  List<String> comments) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.userId = userId;
        this.details = details;
        if (images != null) {
            this.images.clear();
            this.images.addAll(images);
        }
        if (likes != null) {
            this.likes.clear();
            this.likes.addAll(likes);
        }
        if (comments != null) {
            this.comments.clear();
            this.comments.addAll(comments);
        }
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public String getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDetails() {
        return details;
    }

    public List<String> getImages() {
        return images;
    }

    public List<String> getLikes() {
        return likes;
    }

    public List<String> getComments() {
        return comments;
    }

    public boolean isOwner(String userId) {
        return userId.equals(this.userId);
    }

    public int getCommentsCount() {
        if (comments != null) {
            return comments.size();
        }
        return 0;
    }

    public boolean isLiked(String userId) {
        if (likes != null) {
            return likes.contains(userId);
        }
        return false;
    }

    public int getLikesCount() {
        if (likes != null) {
            return likes.size();
        }
        return 0;
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
}
