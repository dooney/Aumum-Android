package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 21/10/2014.
 */
public class DeletePartyEvent {
    private String partyId;

    public DeletePartyEvent(String partyId) {
        this.partyId = partyId;
    }

    public String getPartyId() {
        return partyId;
    }
}
