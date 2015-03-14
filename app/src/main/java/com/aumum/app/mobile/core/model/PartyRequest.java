package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 13/03/2015.
 */
public class PartyRequest extends AggregateRoot {

    protected String deletedAt;
    protected String userId;
    protected String city;
    protected String area;
    protected String type;
    protected String subType;
    protected List<String> members = new ArrayList<String>();
    protected List<String> likes = new ArrayList<String>();

    protected User user;

    public PartyRequest(String userId,
                        String city,
                        String area,
                        String type,
                        String subType) {
        this.userId = userId;
        this.city = city;
        this.area = area;
        this.type = type;
        this.subType = subType;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public String getCity() {
        return city;
    }

    public String getArea() {
        return area;
    }

    public List<String> getMembers() {
        return members;
    }

    public List<String> getLikes() {
        return likes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
