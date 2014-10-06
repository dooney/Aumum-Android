package com.aumum.app.mobile.core;

import android.content.Context;

import com.aumum.app.mobile.Injector;

import javax.inject.Inject;

/**
 * Created by Administrator on 6/10/2014.
 */
public class UserStore {
    private static UserStore instance;

    @Inject BootstrapService bootstrapService;

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
        diskCacheService = new DiskCacheService(context, DISK_CACHE_KEY);
        Injector.inject(this);
    }

    public User getCurrentUser() {
        User user = (User)diskCacheService.get(CURRENT_USER_KEY);
        if (user == null) {
            user = bootstrapService.getCurrentUser();
            saveCurrentUser(user);
        }
        return user;
    }

    public User getUserById(String id) {
        User user = (User)diskCacheService.get(id);
        if (user == null) {
            user = bootstrapService.getUserById(id);
            saveUser(user);
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
