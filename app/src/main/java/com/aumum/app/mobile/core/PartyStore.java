package com.aumum.app.mobile.core;

import android.content.Context;

import com.aumum.app.mobile.Injector;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 30/09/2014.
 */
public class PartyStore {
    private static PartyStore instance;

    @Inject BootstrapService bootstrapService;

    private DiskCacheService diskCacheService;

    private DateTime lastUpdateTime;

    private int limitPerLoad = 25;

    private String DISK_CACHE_KEY = "Party";

    public static PartyStore getInstance(Context context) {
        if (instance == null) {
            instance = new PartyStore(context);
        }
        return instance;
    }

    private PartyStore(Context context) {
        diskCacheService = new DiskCacheService(context, DISK_CACHE_KEY);
        Injector.inject(this);
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
