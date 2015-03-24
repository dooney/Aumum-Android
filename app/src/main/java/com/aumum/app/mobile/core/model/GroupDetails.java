package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 24/03/2015.
 */
public class GroupDetails {

    private String name;
    private User owner;
    private int size;
    private boolean isAdded;

    public GroupDetails(String name, User user, int size, boolean isAdded) {
        this.name = name;
        this.owner = user;
        this.size = size;
        this.isAdded = isAdded;
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    public int getSize() {
        return size;
    }

    public boolean isAdded() {
        return isAdded;
    }
}
