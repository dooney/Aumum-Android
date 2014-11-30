package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.model.AskingReply;
import com.aumum.app.mobile.core.service.RestService;

import java.util.List;

/**
 * Created by Administrator on 30/11/2014.
 */
public class AskingReplyStore {

    private RestService restService;

    public AskingReplyStore(RestService restService) {
        this.restService = restService;
    }

    public List<AskingReply> getAskingReplies(List<String> idList) {
        return restService.getAskingReplies(idList);
    }
}
