package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 3/11/2014.
 */
public class PartyDeletedEvent {
    private String partyId;

    public String getPartyId() {
        return partyId;
    }

    public PartyDeletedEvent(String partyId) {
        this.partyId = partyId;
    }
}
