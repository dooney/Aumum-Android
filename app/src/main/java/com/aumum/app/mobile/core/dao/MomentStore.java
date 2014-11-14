package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.service.RestService;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 8/11/2014.
 */
public class MomentStore {
    @Inject
    RestService restService;

    private int LIMIT_PER_LOAD = 15;

    public MomentStore() {
        Injector.inject(this);
    }

    public List<Moment> getUpwardsList(List<String> idList, String time) {
        List<Moment> partyList;
        if (time != null) {
            partyList = restService.getMomentsAfter(idList, time, Integer.MAX_VALUE);
        } else {
            partyList = restService.getMomentsAfter(idList, null, LIMIT_PER_LOAD);
        }
        return partyList;
    }

    public List<Moment> getBackwardsList(List<String> idList, String time) {
        return restService.getMomentsBefore(idList, time, LIMIT_PER_LOAD);
    }

    public void refresh(List<Moment> momentList) {

    }
}
