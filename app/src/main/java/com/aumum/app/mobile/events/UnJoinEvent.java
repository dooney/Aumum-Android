package com.aumum.app.mobile.events;

import com.aumum.app.mobile.core.model.Message;

/**
 * Created by Administrator on 10/10/2014.
 */
public class UnJoinEvent extends MessageEvent {
    public UnJoinEvent(String partyOwnerId, String userId) {
        super(partyOwnerId, userId);
    }

    @Override
    public int getMessageType() {
        return Message.UNJOIN;
    }
}
