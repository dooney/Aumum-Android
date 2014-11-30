package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.model.Comment;
import com.aumum.app.mobile.core.service.RestService;

import java.util.List;

/**
 * Created by Administrator on 11/10/2014.
 */
public class PartyCommentStore {

    private RestService restService;

    public PartyCommentStore(RestService restService) {
        this.restService = restService;
    }

    public List<Comment> getPartyComments(List<String> idList) {
        return restService.getPartyComments(idList);
    }
}
