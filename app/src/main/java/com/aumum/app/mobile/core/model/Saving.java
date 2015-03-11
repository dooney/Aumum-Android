package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 12/03/2015.
 */
public class Saving extends AggregateRoot {
    private String deletedAt;
    private String userId;
    private double amount;
    private List<String> images = new ArrayList<String>();
    private List<String> likes = new ArrayList<String>();
    private List<String> comments = new ArrayList<String>();

    private User user;

    public Saving(String userId,
                  double amount,
                  List<String> images) {
        this.userId = userId;
        this.amount = amount;
        if (images != null) {
            this.images.clear();
            this.images.addAll(images);
        }
    }

    public Saving(String objectId,
                  String createdAt,
                  String userId,
                  double amount,
                  List<String> images,
                  List<String> likes,
                  List<String> comments) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.userId = userId;
        this.amount = amount;
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

    public double getAmount() {
        return amount;
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

    public String getDetails() {
        return "我省了" + amount + "刀，击败93%的人，你能超过我吗？";
    }
}
