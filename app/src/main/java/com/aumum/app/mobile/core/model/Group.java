package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 21/11/2014.
 */
public class Group {
    private String chatId;
    private String screenName;
    private String avatarUrl;

    public Group(String chatId, String screenName, String avatarUrl) {
        this.chatId = chatId;
        this.screenName = screenName;
        this.avatarUrl = avatarUrl;
    }

    public String getChatId() {
        return chatId;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
