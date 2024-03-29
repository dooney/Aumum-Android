package com.aumum.app.mobile.core.dao.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table PARTY_VM.
 */
public class PartyEntity extends BaseEntity {

    private String userId;
    private String title;
    private String date;
    private String time;
    private String address;
    private Double latitude;
    private Double longitude;
    private String location;
    private String details;
    private String groupId;
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
                       String title,
                       String date,
                       String time,
                       String address,
                       Double latitude,
                       Double longitude,
                       String location,
                       String details,
                       String groupId,
                       String members,
                       String likes,
                       String comments,
                       String reasons,
                       String favorites,
                       String images) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.details = details;
        this.groupId = groupId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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
