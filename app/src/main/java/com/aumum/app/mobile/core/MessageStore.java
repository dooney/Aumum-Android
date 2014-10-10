package com.aumum.app.mobile.core;

import android.content.Context;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.authenticator.ApiKeyProvider;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 7/10/2014.
 */
public class MessageStore {
    @Inject BootstrapService bootstrapService;
    @Inject ApiKeyProvider apiKeyProvider;

    private DiskCacheService diskCacheService;

    private DateTime lastUpdateTime;

    private int limitPerLoad = 10;

    private String diskCacheKey;

    public MessageStore(Context context) {
        Injector.inject(this);
        String userId = apiKeyProvider.getAuthUserId();
        diskCacheKey = "Message_" + userId;
        diskCacheService = DiskCacheService.getInstance(context, diskCacheKey);
    }

    public boolean hasOfflineData() {
        Object data = diskCacheService.get(diskCacheKey);
        if (data != null) {
            List<Message> messageList = (List<Message>) data;
            if (messageList.size() > 0) {
                return true;
            }
        }
        return false;
    }

    public List<Message> getMessageList(List<String> idList) {
        List<Message> messageList;
        if (lastUpdateTime != null) {
            messageList = bootstrapService.getMessagesBefore(idList, lastUpdateTime, Integer.MAX_VALUE);
        } else {
            messageList = bootstrapService.getMessagesBefore(idList, null, limitPerLoad);
        }
        if (messageList.size() > 0) {
            String time = messageList.get(messageList.size() - 1).getCreatedAt();
            lastUpdateTime = new DateTime(time, DateTimeZone.UTC);
        }
        return messageList;
    }

    public void saveOfflineData(Object data) {
        diskCacheService.save(diskCacheKey, data);
    }

    public List<Message> getOfflineList() {
        List<Message> messageList = new ArrayList<Message>();
        Object data = diskCacheService.get(diskCacheKey);
        if (data != null) {
            messageList = (List<Message>) data;
            if (messageList.size() > 0) {
                String time = messageList.get(0).getCreatedAt();
                lastUpdateTime = new DateTime(time, DateTimeZone.UTC);
            }
        }
        return messageList;
    }
}