package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 27/11/2014.
 */
public class Asking extends AggregateRoot {

    private String userId;
    private int category;
    private String question;

    private User user;

    public Asking(String userId,
                  int category,
                  String question) {
        this.userId = userId;
        this.category = category;
        this.question = question;
    }

    public String getUserId() {
        return userId;
    }

    public String getQuestion() {
        return question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
