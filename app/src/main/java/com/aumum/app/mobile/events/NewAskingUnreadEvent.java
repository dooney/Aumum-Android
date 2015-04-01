package com.aumum.app.mobile.events;

import java.util.List;

/**
 * Created by Administrator on 3/03/2015.
 */
public class NewAskingUnreadEvent {

    private List<String> groups;

    public NewAskingUnreadEvent(List<String> groups) {
        this.groups = groups;
}

    public List<String> getGroups() {
        return groups;
    }
}
