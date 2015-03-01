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
    protected List<String> tags = new ArrayList<String>();

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

    public List<String> getTags() {
        return tags;
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

    public void addParty(String partyId) {
        if (parties != null && !parties.contains(partyId)) {
            parties.add(partyId);
        }
    }

    public void removeParty(String partyId) {
        if (parties != null && parties.contains(partyId)) {
            parties.remove(partyId);
        }
    }

    public void addPartyFavorite(String partyId) {
        if (favParties != null && !favParties.contains(partyId)) {
            favParties.add(partyId);
        }
    }

    public void removePartyFavorite(String partyId) {
        if (favParties != null && favParties.contains(partyId)) {
            favParties.remove(partyId);
        }
    }

    public void addAsking(String askingId) {
        if (askings != null && !askings.contains(askingId)) {
            askings.add(askingId);
        }
    }

    public void addAskingFavorite(String askingId) {
        if (favAskings != null && !favAskings.contains(askingId)) {
            favAskings.add(askingId);
        }
    }

    public void removeAskingFavorite(String askingId) {
        if (favAskings != null && favAskings.contains(askingId)) {
            favAskings.remove(askingId);
        }
    }

    public void resetProfile() {
        screenName = "用户" + objectId;
        city = "悉尼";
        area = "Unknown";
    }

    public boolean isContact(String userId) {
        if (contacts != null) {
            return contacts.contains(userId);
        }
        return false;
    }
}
