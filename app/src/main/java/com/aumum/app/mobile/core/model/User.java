package com.aumum.app.mobile.core.model;

import com.aumum.app.mobile.ui.view.sort.InitialSortable;
import com.aumum.app.mobile.utils.Strings;

import java.util.ArrayList;
import java.util.List;

public class User extends AggregateRoot implements InitialSortable {

    protected String username;
    protected String chatId;
    protected String screenName;
    protected String email;
    protected String sessionToken;
    protected String city;
    protected String area;
    protected String avatarUrl;
    protected String about;
    protected List<String> contacts = new ArrayList<String>();
    protected List<String> tags = new ArrayList<>();
    protected Integer credit;

    public User() {

    }

    public User(String objectId,
                String username,
                String chatId,
                String createdAt,
                String screenName,
                String email,
                String city,
                String area,
                String avatarUrl,
                String about,
                List<String> contacts,
                List<String> tags,
                Integer credit) {
        this.objectId = objectId;
        this.username = username;
        this.chatId = chatId;
        this.createdAt = createdAt;
        this.screenName = screenName;
        this.email = email;
        this.city = city;
        this.area = area;
        this.avatarUrl = avatarUrl;
        this.about = about;
        if (contacts != null) {
            this.contacts.clear();
            this.contacts.addAll(contacts);
        }
        if (tags != null) {
            this.tags.clear();
            this.tags.addAll(tags);
        }
        this.credit = credit;
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

    public List<String> getContacts() {
        return contacts;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }

    public Integer getCredit() {
        if (credit != null) {
            return credit;
        }
        return 0;
    }

    public void updateCredit(int delta) {
        this.credit = getCredit() + delta;
    }

    @Override
    public String getSortLetters() {
        String pinyin = Strings.getSelling(screenName);
        String sortString = pinyin.substring(0, 1).toUpperCase();
        if (sortString.matches("[A-Z]")) {
            return sortString.toUpperCase();
        } else {
            return "#";
        }
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
        city = "其他城市";
        area = "其他地区";
    }

    public boolean isContact(String userId) {
        if (contacts != null) {
            return contacts.contains(userId);
        }
        return false;
    }
}
