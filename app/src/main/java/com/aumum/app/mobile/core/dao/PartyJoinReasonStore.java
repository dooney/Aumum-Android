package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.model.PartyJoinReason;
import com.aumum.app.mobile.core.service.RestService;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 28/10/2014.
 */
public class PartyJoinReasonStore {
    @Inject RestService restService;

    public PartyJoinReasonStore() {
        Injector.inject(this);
    }

    public List<PartyJoinReason> getPartyJoinReasons(String partyId) {
        return restService.getPartyJoinReasons(partyId);
    }
}
