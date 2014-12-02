package com.aumum.app.mobile.core.model;

import com.aumum.app.mobile.utils.TimeUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 27/11/2014.
 */
public class Asking extends AggregateRoot {

    private String updatedAt;
    private String userId;
    private int category;
    private String question;
    private List<String> replies = new ArrayList<String>();
    private List<String> favorites = new ArrayList<String>();

    private User user;

    public Asking(String userId,
                  int category,
                  String question) {
        this.userId = userId;
        this.category = category;
        this.question = question;
    }

    public Asking(String objectId,
                  String createdAt,
                  String updatedAt,
                  String userId,
                  int category,
                  String question,
                  List<String> replies,
                  List<String> favorites) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.category = category;
        this.question = question;
        this.replies.clear();
        this.replies.addAll(replies);
        this.favorites.clear();
        this.favorites.addAll(favorites);
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public int getCategory() {
        return category;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getReplies() {
        return replies;
    }

    public List<String> getFavorites() {
        return favorites;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getRepliesCount() {
        if (replies != null) {
            return replies.size();
        }
        return 0;
    }

    public int getFavoritesCount() {
        if (favorites != null) {
            return favorites.size();
        }
        return 0;
    }

    public boolean isOwner(String userId) {
        return userId.equals(this.userId);
    }

    public boolean isFavorited(String userId) {
        if (favorites != null) {
            return favorites.contains(userId);
        }
        return false;
    }

    public String getUpdatedAtFormatted() {
        DateTime time = new DateTime(updatedAt, DateTimeZone.UTC);
        return TimeUtils.getFormattedTimeString(time);
    }
}
