package com.aumum.app.mobile.core.dao.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table PARTY_VM.
 */
public class PartyEntity extends BaseEntity {

    private String userId;
    private String date;
    private String time;
    private Integer age;
    private Integer gender;
    private String title;
    private String place;
    private String details;
    private String members;
    private String likes;
    private String comments;
    private String reasons;
    private String favorites;
    private String images;

    public PartyEntity() {
    }

    public PartyEntity(String objectId) {
        this.objectId = objectId;
    }

    public PartyEntity(String objectId,
                       java.util.Date createdAt,
                       String userId,
                       String date,
                       String time,
                       Integer age,
                       Integer gender,
                       String title,
                       String place,
                       String details,
                       String members,
                       String likes,
                       String comments,
                       String reasons,
                       String favorites,
                       String images) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.userId = userId;
        this.date = date;
        this.time = time;
        this.age = age;
        this.gender = gender;
        this.title = title;
        this.place = place;
        this.details = details;
        this.members = members;
        this.likes = likes;
        this.comments = comments;
        this.reasons = reasons;
        this.favorites = favorites;
        this.images = images;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getReasons() {
        return reasons;
    }

    public void setReasons(String reasons) {
        this.reasons = reasons;
    }

    public String getFavorites() {
        return favorites;
    }

    public void setFavorites(String favorites) {
        this.favorites = favorites;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
