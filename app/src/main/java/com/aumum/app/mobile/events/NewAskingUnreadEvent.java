package com.aumum.app.mobile.events;

import java.util.List;

/**
 * Created by Administrator on 3/03/2015.
 */
public class NewAskingUnreadEvent {

    private List<Integer> categories;

    public NewAskingUnreadEvent(List<Integer> categories) {
        this.categories = categories;
    }
}
