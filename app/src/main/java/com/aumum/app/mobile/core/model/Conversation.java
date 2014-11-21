package com.aumum.app.mobile.core.model;

import com.easemob.chat.EMConversation;

/**
 * Created by Administrator on 21/11/2014.
 */
public class Conversation {
    private User contact;
    private Group group;
    private EMConversation emConversation;

    public Conversation(EMConversation emConversation) {
        this.emConversation = emConversation;
    }

    public void setContact(User contact) {
        this.contact = contact;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getContact() {
        return contact;
    }

    public Group getGroup() {
        return group;
    }

    public EMConversation getEmConversation() {
        return emConversation;
    }
}
