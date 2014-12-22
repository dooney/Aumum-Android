package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 22/12/2014.
 */
public class GroupDetails {

    private ArrayList<User> members;

    public GroupDetails(List<User> members) {
        this.members = new ArrayList<User>();
        this.members.addAll(members);
    }

    public ArrayList<User> getMembers() {
        return members;
    }
}
