package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2/03/2015.
 */
public class Moment extends AggregateRoot {

    private String userId;
    private String details;
    private List<String> images = new ArrayList<String>();

    private User user;

    public Moment(String userId,
                  String details,
                  List<String> images) {
        this.userId = userId;
        this.details = details;
        this.images.clear();
        this.images.addAll(images);
    }

    public String getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDetails() {
        return details;
    }

    public List<String> getImages() {
        return images;
    }
}
