package com.aumum.app.mobile.events;

import com.aumum.app.mobile.core.model.Party;

/**
 * Created by Administrator on 28/10/2014.
 */
public class AddPartyReasonFinishedEvent {
    private int type;
    private Party party;

    public AddPartyReasonFinishedEvent(int type, Party party) {
        this.type = type;
        this.party = party;
    }

    public int getType() {
        return type;
    }

    public Party getParty() {
        return party;
    }
}
