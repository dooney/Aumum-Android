package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.model.Comment;
import com.aumum.app.mobile.core.service.RestService;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 11/10/2014.
 */
public class PartyCommentStore {
    @Inject
    RestService restService;

    public PartyCommentStore() {
        Injector.inject(this);
    }

    public List<Comment> getPartyComments(String partyId) {
        return restService.getPartyComments(partyId);
    }
}
