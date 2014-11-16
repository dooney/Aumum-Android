package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

public class User extends AggregateRoot {
    protected String screenName;
    protected String sessionToken;
    protected Boolean emailVerified;
    protected int area;
    protected String avatarUrl;
    protected String about;
    protected List<String> followers = new ArrayList<String>();
    protected List<String> followings = new ArrayList<String>();
    protected List<String> comments = new ArrayList<String>();
    protected List<String> messages = new ArrayList<String>();
    protected List<String> parties = new ArrayList<String>();
    protected List<String> partyPosts = new ArrayList<String>();
    protected List<String> moments = new ArrayList<String>();
    protected List<String> momentPosts = new ArrayList<String>();

    public User() {

    }

    public User(String objectId,
                String createdAt,
                String screenName,
                int area,
                String avatarUrl,
                String about,
                List<String> followers,
                List<String> followings,
                List<String> comments,
                List<String> messages,
                List<String> parties,
                List<String> partyPosts,
                List<String> moments,
                List<String> momentPosts) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.screenName = screenName;
        this.area = area;
        this.avatarUrl = avatarUrl;
        this.about = about;
        this.followers.clear();
        this.followers.addAll(followers);
        this.followings.clear();
        this.followings.addAll(followings);
        this.comments.clear();
        this.comments.addAll(comments);
        this.messages.clear();
        this.messages.addAll(messages);
        this.parties.clear();
        this.parties.addAll(parties);
        this.partyPosts.clear();
        this.partyPosts.addAll(partyPosts);
        this.moments.clear();
        this.moments.addAll(moments);
        this.momentPosts.clear();
        this.momentPosts.addAll(momentPosts);
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

    public String getAbout() {
        return about;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public List<String> getFollowings() {
        return followings;
    }

    public List<String> getComments() {
        return comments;
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<String> getParties() {
        return parties;
    }

    public List<String> getPartyPosts() {
        return partyPosts;
    }

    public List<String> getMoments() {
        return moments;
    }

    public List<String> getMomentPosts() {
        return momentPosts;
    }

    public boolean isLatestMessage(String messageId) {
        return messages.indexOf(messageId) + 1 == messages.size();
    }
}
