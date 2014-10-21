package com.aumum.app.mobile.core.dao;

import android.content.Context;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.infra.cache.DiskCache;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.service.RestService;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 30/09/2014.
 */
public class PartyStore {
    @Inject
    RestService restService;
    @Inject
    ApiKeyProvider apiKeyProvider;

    private DiskCache diskCacheService;

    private int LIMIT_PER_LOAD = 15;

    private String diskCacheKey;

    public PartyStore(Context context) {
        Injector.inject(this);
        String userId = apiKeyProvider.getAuthUserId();
        diskCacheKey = "Party_" + userId;
        diskCacheService = DiskCache.getInstance(context, diskCacheKey);
    }

    public List<Party> getUpwardsList(String time) {
        List<Party> partyList;
        if (time != null) {
            DateTime after = new DateTime(time, DateTimeZone.UTC);
            partyList = restService.getPartiesAfter(after, Integer.MAX_VALUE);
        } else {
            partyList = restService.getPartiesAfter(null, LIMIT_PER_LOAD);
        }
        return partyList;
    }

    public List<Party> getBackwardsList(String time) {
        DateTime before = new DateTime(time, DateTimeZone.UTC);
        return restService.getPartiesBefore(before, LIMIT_PER_LOAD);
    }

    public void saveOfflineData(Object data) {
        diskCacheService.save(diskCacheKey, data);
    }

    public List<Party> getOfflineList() {
        List<Party> partyList = new ArrayList<Party>();
        Object data = diskCacheService.get(diskCacheKey);
        if (data != null) {
            partyList = (List<Party>) data;
        }
        return partyList;
    }

    public void refresh(List<Party> partyList) {
        if (partyList != null && partyList.size() > 0) {
            List<String> partyIds = new ArrayList<String>();
            for (Party party : partyList) {
                partyIds.add(party.getObjectId());
            }
            List<Party> resultList = restService.refreshParties(partyIds);
            HashMap<String, Party> hashMap = new HashMap<String, Party>();
            for (Party party : resultList) {
                hashMap.put(party.getObjectId(), party);
            }
            for (Party party: partyList) {
                Party result = hashMap.get(party.getObjectId());
                if (result != null) {
                    party.getMembers().clear();
                    party.getMembers().addAll(result.getMembers());

                    party.getComments().clear();
                    party.getComments().addAll(result.getComments());

                    party.getFans().clear();
                    party.getFans().addAll(result.getFans());
                }
            }
        }
    }

    public Party getPartyById(String partyId) {
        return restService.getPartyById(partyId);
    }
}
