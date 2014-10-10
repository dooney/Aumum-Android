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
 * Created by Administrator on 30/09/2014.
 */
public class PartyStore {
    @Inject BootstrapService bootstrapService;
    @Inject ApiKeyProvider apiKeyProvider;

    private DiskCacheService diskCacheService;

    private DateTime lastUpdateTime;

    private int limitPerLoad = 25;

    private String diskCacheKey;

    public PartyStore(Context context) {
        Injector.inject(this);
        String userId = apiKeyProvider.getAuthUserId();
        diskCacheKey = "Party_" + userId;
        diskCacheService = DiskCacheService.getInstance(context, diskCacheKey);
    }

    public List<Party> getUpwardsList() {
        List<Party> partyList;
        if (lastUpdateTime != null) {
            partyList = bootstrapService.getPartiesAfter(lastUpdateTime, Integer.MAX_VALUE);
        } else {
            partyList = bootstrapService.getPartiesAfter(null, limitPerLoad);
        }
        lastUpdateTime = DateTime.now(DateTimeZone.UTC);
        return partyList;
    }

    public List<Party> getBackwardsList(String time) {
        DateTime before = new DateTime(time, DateTimeZone.UTC);
        return bootstrapService.getPartiesBefore(before, limitPerLoad);
    }

    public boolean hasOfflineData() {
        Object data = diskCacheService.get(diskCacheKey);
        if (data != null) {
            List<Party> partyList = (List<Party>) data;
            if (partyList.size() > 0) {
                return true;
            }
        }
        return false;
    }

    public void saveOfflineData(Object data) {
        diskCacheService.save(diskCacheKey, data);
    }

    public List<Party> getOfflineList() {
        List<Party> partyList = new ArrayList<Party>();
        Object data = diskCacheService.get(diskCacheKey);
        if (data != null) {
            partyList = (List<Party>) data;
            if (partyList.size() > 0) {
                String time = partyList.get(0).getCreatedAt();
                lastUpdateTime = new DateTime(time, DateTimeZone.UTC);
            }
        }
        return partyList;
    }
}
