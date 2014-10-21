package com.aumum.app.mobile.core.dao;

import android.content.Context;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.infra.cache.DiskCache;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.service.RestService;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 7/10/2014.
 */
public class MessageStore {
    @Inject RestService restService;
    @Inject ApiKeyProvider apiKeyProvider;

    private DiskCache diskCacheService;

    private final int LIMIT_PER_LOAD = 10;

    private String diskCacheKey;

    public MessageStore(Context context) {
        Injector.inject(this);
        String userId = apiKeyProvider.getAuthUserId();
        diskCacheKey = "Message_" + userId;
        diskCacheService = DiskCache.getInstance(context, diskCacheKey);
    }

    public List<Message> getUpwardsList(List<String> idList, String time) {
        List<Message> messageList;
        if (time != null) {
            DateTime after = new DateTime(time, DateTimeZone.UTC);
            messageList = restService.getMessagesAfter(idList, after, Integer.MAX_VALUE);
        } else {
            messageList = restService.getMessagesAfter(idList, null, LIMIT_PER_LOAD);
        }
        return messageList;
    }

    public List<Message> getBackwardsList(List<String> idList, String time) {
        DateTime before = new DateTime(time, DateTimeZone.UTC);
        return restService.getMessagesBefore(idList, before, LIMIT_PER_LOAD);
    }

    public void saveOfflineData(Object data) {
        diskCacheService.save(diskCacheKey, data);
    }

    public List<Message> getOfflineList() {
        List<Message> messageList = new ArrayList<Message>();
        Object data = diskCacheService.get(diskCacheKey);
        if (data != null) {
            messageList = (List<Message>) data;
        }
        return messageList;
    }
}