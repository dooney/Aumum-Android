package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 25/09/2014.
 */
public class Party extends AggregateRoot {
    protected String userId;
    protected Date date;
    protected Time time;
    protected int age;
    protected int gender;
    protected String title;
    protected int area;
    protected String location;
    protected String details;
    protected User user;
    protected List<String> members = new ArrayList<String>();
    protected List<String> fans = new ArrayList<String>();
    protected List<String> comments = new ArrayList<String>();
    protected List<String> joinReasons = new ArrayList<String>();

    public Party() {
        date = new Date();
        time = new Time();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getLocation() {
        return location;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getMembers() {
        return members;
    }

    public List<String> getFans() {
        return fans;
    }

    public List<String> getComments() { return comments; }

    public List<String> getJoinReasons() {
        return joinReasons;
    }

    public boolean validate() {
        return date != null && time != null && !title.isEmpty() && !location.isEmpty() && !details.isEmpty();
    }

    public boolean isOwner(String userId) {
        return userId.equals(this.userId);
    }

    public boolean isMember(String userId) {
        if (members != null) {
            return members.contains(userId);
        }
        return false;
    }

    public int getCommentCounts() {
        if (comments != null) {
            return comments.size();
        }
        return 0;
    }

    public boolean isLike(String userId) {
        if (fans != null) {
            return fans.contains(userId);
        }
        return false;
    }

    public int getLikes() {
        if (fans != null) {
            return fans.size();
        }
        return 0;
    }
}
