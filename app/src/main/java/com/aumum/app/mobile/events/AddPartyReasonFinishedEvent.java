package com.aumum.app.mobile.events;

import com.aumum.app.mobile.core.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 28/10/2014.
 */
public class AddPartyReasonFinishedEvent {
    private int type;
    private List<User> members;

    public AddPartyReasonFinishedEvent(int type, List<User> members) {
        this.type = type;
        this.members = new ArrayList<User>();
        this.members.addAll(members);
    }

    public int getType() {
        return type;
    }

    public List<User> getMembers() {
        return members;
    }
}
