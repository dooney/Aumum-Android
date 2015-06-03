package com.aumum.app.mobile.core.model;

import com.easemob.chat.EMConversation;

/**
 * Created by Administrator on 21/11/2014.
 */
public class Conversation {
    private UserInfo contact;
    private EMConversation emConversation;

    public Conversation(EMConversation emConversation) {
        this.emConversation = emConversation;
    }

    public void setContact(UserInfo contact) {
        this.contact = contact;
    }

    public UserInfo getContact() {
        return contact;
    }

    public EMConversation getEmConversation() {
        return emConversation;
    }
}
