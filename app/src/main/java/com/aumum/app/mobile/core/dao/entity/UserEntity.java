package com.aumum.app.mobile.core.dao.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

import java.util.Date;

/**
 * Entity mapped to table USER_VM.
 */
public class UserEntity extends BaseEntity {

    private Long id;
    private String screenName;
    private Integer area;
    private String avatarUrl;
    private String about;
    private String followers;
    private String followings;
    private String comments;
    private String messages;
    private String parties;
    private String partyPosts;
    private String moments;
    private String momentPosts;

    public UserEntity() {
    }

    public UserEntity(Long id) {
        this.id = id;
    }

    public UserEntity(Long id,
                      String instanceId,
                      String objectId,
                      Date createdAt,
                      String screenName,
                      Integer area,
                      String avatarUrl,
                      String about,
                      String followers,
                      String followings,
                      String comments,
                      String messages,
                      String parties,
                      String partyPosts,
                      String moments,
                      String momentPosts) {
        this.id = id;
        this.instanceId = instanceId;
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.screenName = screenName;
        this.area = area;
        this.avatarUrl = avatarUrl;
        this.about = about;
        this.followers = followers;
        this.followings = followings;
        this.comments = comments;
        this.messages = messages;
        this.parties = parties;
        this.partyPosts = partyPosts;
        this.moments = moments;
        this.momentPosts = momentPosts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
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

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowings() {
        return followings;
    }

    public void setFollowings(String followings) {
        this.followings = followings;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getParties() {
        return parties;
    }

    public void setParties(String parties) {
        this.parties = parties;
    }

    public String getPartyPosts() {
        return partyPosts;
    }

    public void setPartyPosts(String partyPosts) {
        this.partyPosts = partyPosts;
    }

    public String getMoments() {
        return moments;
    }

    public void setMoments(String moments) {
        this.moments = moments;
    }

    public String getMomentPosts() {
        return momentPosts;
    }

    public void setMomentPosts(String momentPosts) {
        this.momentPosts = momentPosts;
    }
}
