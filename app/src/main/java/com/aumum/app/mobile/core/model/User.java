package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

public class User extends AggregateRoot {
    protected String chatId;
    protected String screenName;
    protected String sessionToken;
    protected Boolean emailVerified;
    protected int area;
    protected String avatarUrl;
    protected String about;
    protected List<String> contacts = new ArrayList<String>();
    protected List<String> messages = new ArrayList<String>();
    protected List<String> parties = new ArrayList<String>();
    protected List<String> askings = new ArrayList<String>();

    public User() {

    }

    public User(String objectId,
                String chatId,
                String createdAt,
                String screenName,
                int area,
                String avatarUrl,
                String about,
                List<String> contacts,
                List<String> messages,
                List<String> parties,
                List<String> askings) {
        this.objectId = objectId;
        this.chatId = chatId;
        this.createdAt = createdAt;
        this.screenName = screenName;
        this.area = area;
        this.avatarUrl = avatarUrl;
        this.about = about;
        this.contacts.clear();
        this.contacts.addAll(contacts);
        this.messages.clear();
        this.messages.addAll(messages);
        this.parties.clear();
        this.parties.addAll(parties);
        this.askings.clear();
        this.askings.addAll(askings);
    }

    public String getChatId() {
        return chatId;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public int getArea() {
        return area;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAbout() {
        return about;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<String> getParties() {
        return parties;
    }

    public List<String> getAskings() {
        return askings;
    }
}
