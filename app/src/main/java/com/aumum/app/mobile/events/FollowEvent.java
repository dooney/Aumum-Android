package com.aumum.app.mobile.events;

import com.aumum.app.mobile.core.Message;

/**
 * Created by Administrator on 7/10/2014.
 */
public class FollowEvent {
    private String followedUserId;
    private String followingUserId;

    public FollowEvent(String followedUserId, String followingUserId) {
        this.followedUserId = followedUserId;
        this.followingUserId = followingUserId;
    }

    public String getFollowedUserId() {
        return followedUserId;
    }

    public String getFollowingUserId() {
        return followingUserId;
    }

    public int getMessageType() {
        return Message.FOLLOW;
    }
}
