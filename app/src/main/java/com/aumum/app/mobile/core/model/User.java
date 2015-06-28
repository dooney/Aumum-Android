package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

public class User extends AggregateRoot {

    protected String username;
    protected String chatId;
    protected String screenName;
    protected String email;
    protected String sessionToken;
    protected String country;
    protected String city;
    protected String area;
    protected String avatarUrl;
    protected String about;
    protected ArrayList<String> contacts = new ArrayList<>();

    public User() {

    }

    public User(String objectId,
                String username,
                String chatId,
                String createdAt,
                String screenName,
                String email,
                String country,
                String city,
                String area,
                String avatarUrl,
                String about,
                List<String> contacts) {
        this.objectId = objectId;
        this.username = username;
        this.chatId = chatId;
        this.createdAt = createdAt;
        this.screenName = screenName;
        this.email = email;
        this.country = country;
        this.city = city;
        this.area = area;
        this.avatarUrl = avatarUrl;
        this.about = about;
        if (contacts != null) {
            this.contacts.clear();
            this.contacts.addAll(contacts);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getChatId() {
        if (chatId != null) {
            return chatId;
        } else if (objectId != null) {
            return objectId.toLowerCase();
        }
        return null;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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

    public void setAbout(String about) {
        this.about = about;
    }

    public ArrayList<String> getContacts() {
        return contacts;
    }

    public void addContact(String contactId) {
        if (contacts != null && !contacts.contains(contactId)) {
            contacts.add(contactId);
        }
    }

    public void removeContact(String contactId) {
        if (contacts != null && contacts.contains(contactId)) {
            contacts.remove(contactId);
        }
    }

    public void resetProfile() {
        screenName = "用户" + objectId;
        country = "其他国家";
        city = "其他城市";
        area = "其他地区";
    }

    public boolean isContact(String userId) {
        if (contacts != null) {
            return contacts.contains(userId);
        }
        return false;
    }

    public String getAddress() {
        return area + " " + city + " " + country;
    }
}
