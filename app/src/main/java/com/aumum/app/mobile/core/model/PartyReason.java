package com.aumum.app.mobile.core.model;

import com.aumum.app.mobile.core.dao.vm.UserVM;

/**
 * Created by Administrator on 28/10/2014.
 */
public class PartyReason extends AggregateRoot {
    private int type;
    private String content;
    private String userId;
    private UserVM user;

    public static final int JOIN = 0;
    public static final int QUIT = 1;
    public static final String ACTION_OPTIONS[] = {
        "报名了该聚会",
        "退出了该聚会"
    };

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public UserVM getUser() {
        return user;
    }

    public void setUser(UserVM user) {
        this.user = user;
    }

    public String getActionText() {
        return ACTION_OPTIONS[type];
    }
}
