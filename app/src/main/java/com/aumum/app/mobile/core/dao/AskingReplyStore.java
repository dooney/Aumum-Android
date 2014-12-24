package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.model.AskingReply;
import com.aumum.app.mobile.core.service.RestService;

import java.util.List;

/**
 * Created by Administrator on 30/11/2014.
 */
public class AskingReplyStore {

    private RestService restService;

    public static final int LIMIT_PER_LOAD = 15;

    public AskingReplyStore(RestService restService) {
        this.restService = restService;
    }

    public List<AskingReply> getUpwardsList(List<String> idList) {
        return restService.getAskingReplies(idList, LIMIT_PER_LOAD);
    }

    public List<AskingReply> getBackwardsList(List<String> idList, String time) {
        return restService.getAskingRepliesBefore(idList, time, LIMIT_PER_LOAD);
    }
}
