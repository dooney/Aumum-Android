package com.aumum.app.mobile.core.model;

import com.aumum.app.mobile.ui.view.sort.Sortable;
import com.aumum.app.mobile.utils.Strings;

import java.util.ArrayList;
import java.util.List;

public class User extends AggregateRoot implements Sortable {

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
    protected List<String> parties = new ArrayList<String>();
    protected List<String> askings = new ArrayList<String>();
    protected List<String> favParties = new ArrayList<String>();
    protected List<String> favAskings = new ArrayList<String>();

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
                List<String> parties,
                List<String> askings,
                List<String> favParties,
                List<String> favAskings) {
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
        this.contacts.clear();
        this.contacts.addAll(contacts);
        this.parties.clear();
        this.parties.addAll(parties);
        this.askings.clear();
        this.askings.addAll(askings);
        this.favParties.clear();
        this.favParties.addAll(favParties);
        this.favAskings.clear();
        this.favAskings.addAll(favAskings);
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

    public List<String> getParties() {
        return parties;
    }

    public List<String> getAskings() {
        return askings;
    }

    public List<String> getFavParties() {
        return favParties;
    }

    public List<String> getFavAskings() {
        return favAskings;
    }

    @Override
    public String getSortLetters() {
        String pinyin = Strings.getSelling(screenName);
        String sortString = pinyin.substring(0, 1).toUpperCase();
        if(sortString.matches("[A-Z]")){
            return sortString.toUpperCase();
        }else{
            return "#";
        }
    }
}
