package com.aumum.app.mobile.events;

import com.aumum.app.mobile.core.Message;

/**
 * Created by Administrator on 10/10/2014.
 */
public class LikeEvent extends MessageEvent {
    public LikeEvent(String partyOwnerId, String userId) {
        super(partyOwnerId, userId);
    }

    @Override
    public int getMessageType() {
        return Message.LIKE;
    }
}
