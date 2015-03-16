package com.aumum.app.mobile.core.model;

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

    public PartyRequest(String objectId,
                        String createdAt,
                        String userId,
                        String city,
                        String area,
                        String type,
                        String subType) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.userId = userId;
        this.city = city;
        this.area = area;
        this.type = type;
        this.subType = subType;
    }

    public String getUserId() {
        return userId;
    }

    public String getCity() {
        return city;
    }

    public String getArea() {
        return area;
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDetails() {
        String details = "求" + city;
        if (area != null) {
            details += area + "区";
        }
        if (subType != null) {
            details += subType;
        }
        details += type;
        return details;
    }

    public boolean isOwner(String userId) {
        return userId.equals(this.userId);
    }
}
