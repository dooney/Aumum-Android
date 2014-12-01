package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 27/11/2014.
 */
public class Asking extends AggregateRoot {

    private String userId;
    private int category;
    private String question;
    private List<String> replies = new ArrayList<String>();

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
                  String userId,
                  int category,
                  String question,
                  List<String> replies) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.userId = userId;
        this.category = category;
        this.question = question;
        this.replies.clear();
        this.replies.addAll(replies);
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getRepliesCount() {
        return replies.size();
    }

    public boolean isOwner(String userId) {
        return userId.equals(this.userId);
    }
}
