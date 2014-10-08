package com.aumum.app.mobile.events;

import com.aumum.app.mobile.core.User;

/**
 * Created by Administrator on 7/10/2014.
 */
public class FollowEvent {
    private String followedUserId;
    private User followingUser;

    public FollowEvent(String followedUserId, User followingUser) {
        this.followedUserId = followedUserId;
        this.followingUser = followingUser;
    }

    public String getFollowedUserId() {
        return followedUserId;
    }

    public String getMessage() {
        if (followingUser != null) {
            return followingUser.getUsername() + " 关注了你";
        }
        return null;
    }
}
