package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 21/11/2014.
 */
public class Group {
    private String chatId;
    private String screenName;

    public Group(String chatId, String screenName) {
        this.chatId = chatId;
        this.screenName = screenName;
    }

    public String getChatId() {
        return chatId;
    }

    public String getScreenName() {
        return screenName;
    }
}
