package com.aumum.app.mobile.core.model;

import com.easemob.chat.EMConversation;

/**
 * Created by Administrator on 21/11/2014.
 */
public class Conversation {
    private UserInfo contact;
    private Group group;
    private EMConversation emConversation;

    public Conversation(EMConversation emConversation) {
        this.emConversation = emConversation;
    }

    public void setContact(UserInfo contact) {
        this.contact = contact;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public UserInfo getContact() {
        return contact;
    }

    public Group getGroup() {
        return group;
    }

    public EMConversation getEmConversation() {
        return emConversation;
    }
}
