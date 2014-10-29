package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.model.PartyReason;
import com.aumum.app.mobile.core.service.RestService;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 28/10/2014.
 */
public class PartyReasonStore {
    @Inject RestService restService;

    public PartyReasonStore() {
        Injector.inject(this);
    }

    public List<PartyReason> getPartyReasons(String partyId) {
        return restService.getPartyReasons(partyId);
    }
}
