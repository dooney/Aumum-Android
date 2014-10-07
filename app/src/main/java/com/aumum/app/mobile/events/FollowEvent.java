package com.aumum.app.mobile.events;

import com.aumum.app.mobile.core.User;

/**
 * Created by Administrator on 7/10/2014.
 */
public class FollowEvent {
    private User user;

    public FollowEvent(User user) {
        this.user = user;
    }

    public User getFollowedUser() {
        return user;
    }

    public String getMessage() {
        if (user != null) {
            return user.getUsername() + " 关注了你";
        }
        return null;
    }
}
