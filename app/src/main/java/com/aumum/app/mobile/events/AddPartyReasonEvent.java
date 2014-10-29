package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 28/10/2014.
 */
public class AddPartyReasonEvent {
    private int type;
    private String reason;

    public AddPartyReasonEvent(int type, String reason) {
        this.type = type;
        this.reason = reason;
    }

    public int getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }
}
