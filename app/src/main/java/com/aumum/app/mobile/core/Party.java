package com.aumum.app.mobile.core;

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

    public boolean validate() {
        return date != null && time != null && !title.isEmpty() && !location.isEmpty() && !details.isEmpty();
    }

    public boolean isJoin(String userId) {
        return false;
    }

    public int getJoins() {
        return 0;
    }

    public int getComments() {
        return 0;
    }

    public boolean isLike(String userId) {
        return false;
    }

    public int getLikes() {
        return 0;
    }
}
