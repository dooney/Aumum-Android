package com.aumum.app.mobile.events;

import com.aumum.app.mobile.core.Message;

/**
 * Created by Administrator on 7/10/2014.
 */
public class FollowEvent extends MessageEvent {

    public FollowEvent(String followedUserId, String followingUserId) {
        super(followedUserId, followingUserId);
    }

    @Override
    public int getMessageType() {
        return Message.FOLLOW;
    }
}
