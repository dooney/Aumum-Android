package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 28/10/2014.
 */
public class AddPartyJoinReasonEvent {
    private String reason;

    public AddPartyJoinReasonEvent(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
