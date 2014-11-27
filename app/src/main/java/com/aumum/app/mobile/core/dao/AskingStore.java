package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.service.RestService;

import java.util.List;

/**
 * Created by Administrator on 27/11/2014.
 */
public class AskingStore {

    RestService restService;

    private int LIMIT_PER_LOAD = 15;

    public AskingStore(RestService restService) {
        this.restService = restService;
    }

    public List<Asking> getUpwardsList(int category, String time) {
        return restService.getAskingListAfter(category, time, LIMIT_PER_LOAD);
    }
}
