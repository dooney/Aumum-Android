package com.aumum.app.mobile.core;

import java.io.Serializable;

/**
 * Created by Administrator on 25/09/2014.
 */
public class Party implements Serializable {
    protected Date date;
    protected Time time;
    protected int age;
    protected int gender;
    protected String title;
    protected String location;
    protected String details;

    public Party() {
        date = new Date();
        time = new Time();
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

    public boolean validate() {
        return date != null && time != null && !title.isEmpty() && !location.isEmpty() && !details.isEmpty();
    }
}
