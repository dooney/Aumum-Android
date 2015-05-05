package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2/03/2015.
 */
public class Moment extends AggregateRoot implements RefreshItem {

    private String deletedAt;
    private String userId;
    private List<String> likes = new ArrayList<String>();
    private List<String> comments = new ArrayList<String>();
    private String text;
    private String imageUrl;

    private UserInfo user;

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
                  List<String> comments,
                  String text,
                  String imageUrl) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.userId = userId;
        if (likes != null) {
            this.likes.clear();
            this.likes.addAll(likes);
        }
        if (comments != null) {
            this.comments.clear();
            this.comments.addAll(comments);
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

    public List<String> getComments() {
        return comments;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
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

    public void addComment(String commentId) {
        if (comments != null && !comments.contains(commentId)) {
            comments.add(commentId);
        }
    }

    public void removeComment(String commentId) {
        if (comments != null && comments.contains(commentId)) {
            comments.remove(commentId);
        }
    }
}