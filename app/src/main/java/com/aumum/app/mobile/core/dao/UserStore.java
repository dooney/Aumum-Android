package com.aumum.app.mobile.core.dao;

import android.content.Context;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.infra.cache.DiskCache;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;

import javax.inject.Inject;

/**
 * Created by Administrator on 6/10/2014.
 */
public class UserStore {
    private static UserStore instance;

    @Inject
    RestService restService;
    @Inject
    ApiKeyProvider apiKeyProvider;

    private DiskCache diskCacheService;

    private String DISK_CACHE_KEY = "User";

    public static UserStore getInstance(Context context) {
        if (instance == null) {
            instance = new UserStore(context);
        }
        return instance;
    }

    private UserStore(Context context) {
        diskCacheService = DiskCache.getInstance(context, DISK_CACHE_KEY);
        Injector.inject(this);
    }

    public User getCurrentUser(boolean refresh) {
        String currentUserId = apiKeyProvider.getAuthUserId();
        return getUserById(currentUserId, refresh);
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

    public void saveUser(User user) {
        diskCacheService.save(user.getObjectId(), user);
    }
}