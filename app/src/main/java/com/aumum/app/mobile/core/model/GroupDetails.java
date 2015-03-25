package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 24/03/2015.
 */
public class GroupDetails {

    private String id;
    private String name;
    private User owner;
    private int size;
    private boolean isMember;

    public GroupDetails(String id,
                        String name,
                        User user,
                        int size,
                        boolean isMember) {
        this.id = id;
        this.name = name;
        this.owner = user;
        this.size = size;
        this.isMember = isMember;
    }

    public String getId() {
        return id;
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

    public boolean isMember() {
        return isMember;
    }
}
