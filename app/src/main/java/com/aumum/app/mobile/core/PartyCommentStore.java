package com.aumum.app.mobile.core;

import com.aumum.app.mobile.Injector;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 11/10/2014.
 */
public class PartyCommentStore {
    @Inject BootstrapService bootstrapService;

    public PartyCommentStore() {
        Injector.inject(this);
    }

    public List<PartyComment> getPartyComments(String partyId) {
        return bootstrapService.getPartyComments(partyId);
    }
}
