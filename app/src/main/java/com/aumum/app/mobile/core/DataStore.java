package com.aumum.app.mobile.core;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 30/09/2014.
 */
public class DataStore {
    private BootstrapService bootstrapService;

    private DiskCacheService diskCacheService;

    private DateTime lastUpdateTime;

    private int limitPerLoad = 25;

    private String DISK_CACHE_KEY = "Party";

    public DataStore(Context context) {
        diskCacheService = new DiskCacheService(context, DISK_CACHE_KEY);
    }

    public void setBootstrapService(BootstrapService service) {
        bootstrapService = service;
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
        return diskCacheService.hasKey(DISK_CACHE_KEY);
    }

    public void saveOfflineData(Object data) {
        diskCacheService.save(DISK_CACHE_KEY, data);
    }

    public List<Party> getOfflineList() {
        List<Party> partyList = new ArrayList<Party>();
        Object data = diskCacheService.get(DISK_CACHE_KEY);
        if (data != null) {
            partyList = (List<Party>) data;
            String time = partyList.get(0).getCreatedAt();
            lastUpdateTime = new DateTime(time, DateTimeZone.UTC);
        }
        return partyList;
    }
}
