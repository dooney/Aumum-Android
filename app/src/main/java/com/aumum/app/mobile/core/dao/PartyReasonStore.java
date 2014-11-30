package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.model.PartyReason;
import com.aumum.app.mobile.core.service.RestService;

import java.util.List;

/**
 * Created by Administrator on 28/10/2014.
 */
public class PartyReasonStore {
    private RestService restService;

    public PartyReasonStore(RestService restService) {
        this.restService = restService;
    }

    public List<PartyReason> getPartyReasons(List<String> idList) {
        return restService.getPartyReasons(idList);
    }
}
