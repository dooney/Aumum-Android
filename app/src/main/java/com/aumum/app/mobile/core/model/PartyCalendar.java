package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 13/01/2015.
 */
public class PartyCalendar {

    private List<Party> partyList;

    public PartyCalendar(List<Party> partyList) {
        this.partyList = new ArrayList<Party>();
        this.partyList.addAll(partyList);
    }

    public List<Party> getPartyList() {
        return partyList;
    }
}
