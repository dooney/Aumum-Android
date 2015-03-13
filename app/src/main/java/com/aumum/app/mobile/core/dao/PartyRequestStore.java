package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.model.PartyRequest;
import com.aumum.app.mobile.core.service.RestService;

import java.util.List;

/**
 * Created by Administrator on 13/03/2015.
 */
public class PartyRequestStore {

    private RestService restService;

    public static final int LIMIT_PER_LOAD = 15;

    public PartyRequestStore(RestService restService) {
        this.restService = restService;
    }

    public List<PartyRequest> getUpwardsList(String time) throws Exception {
        int limit = time != null ? Integer.MAX_VALUE : LIMIT_PER_LOAD;
        return restService.getPartyRequestsAfter(time, limit);
    }

    public List<PartyRequest> getBackwardsList(String time) throws Exception {
        return restService.getPartyRequestsBefore(time, LIMIT_PER_LOAD);
    }
}
