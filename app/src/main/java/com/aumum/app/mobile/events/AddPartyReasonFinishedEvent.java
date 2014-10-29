package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 28/10/2014.
 */
public class AddPartyReasonFinishedEvent {
    private int type;

    public AddPartyReasonFinishedEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
