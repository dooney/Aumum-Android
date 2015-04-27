package com.aumum.app.mobile.core.model;

import com.easemob.chat.EMMessage;

/**
 * Created by Administrator on 27/04/2015.
 */
public class ChatMessage {

    private UserInfo userInfo;
    private EMMessage message;

    public ChatMessage(UserInfo userInfo,
                       EMMessage message) {
        this.userInfo = userInfo;
        this.message = message;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public EMMessage getMessage() {
        return message;
    }
}
