package com.aumum.app.mobile.core.model;

import com.aumum.app.mobile.core.dao.vm.UserVM;

/**
 * Created by Administrator on 8/11/2014.
 */
public class Moment extends AggregateRoot {
    private String text;
    private String userId;
    private UserVM user;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserVM getUser() {
        return user;
    }

    public void setUser(UserVM user) {
        this.user = user;
    }
}
