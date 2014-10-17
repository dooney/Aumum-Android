package com.aumum.app.mobile.core;

import android.content.Context;

import com.aumum.app.mobile.Injector;

import javax.inject.Inject;

/**
 * Created by Administrator on 6/10/2014.
 */
public class UserStore {
    private static UserStore instance;

    @Inject
    RestService restService;

    private DiskCacheService diskCacheService;

    private String DISK_CACHE_KEY = "User";

    private String CURRENT_USER_KEY = "IAmTheCurrentUser";

    public static UserStore getInstance(Context context) {
        if (instance == null) {
            instance = new UserStore(context);
        }
        return instance;
    }

    private UserStore(Context context) {
        diskCacheService = DiskCacheService.getInstance(context, DISK_CACHE_KEY);
        Injector.inject(this);
    }

    private User getCurrentUser() {
        User user = restService.getCurrentUser();
        saveCurrentUser(user);
        return user;
    }

    public User getCurrentUser(boolean refresh) {
        if (refresh) {
            return getCurrentUser();
        }
        User user = (User)diskCacheService.get(CURRENT_USER_KEY);
        if (user == null) {
            return getCurrentUser();
        }
        return user;
    }

    private User getUserById(String id) {
        User user = restService.getUserById(id);
        saveUser(user);
        return user;
    }

    public User getUserById(String id, boolean refresh) {
        if (refresh) {
            return getUserById(id);
        }
        User user = (User)diskCacheService.get(id);
        if (user == null) {
            return getUserById(id);
        }
        return user;
    }

    public void saveCurrentUser(User user) {
        diskCacheService.save(CURRENT_USER_KEY, user);
    }

    public void saveUser(User user) {
        diskCacheService.save(user.getObjectId(), user);
    }
}