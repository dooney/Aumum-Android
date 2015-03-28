package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 28/03/2015.
 */
public class CreditGift extends AggregateRoot {

    private String deletedAt;
    private int cost;
    private int seq;
    private String screenName;
    private String avatarUrl;
    private String description;
    private String country;

    public int getCost() {
        return cost;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getCountry() {
        return country;
    }
}
