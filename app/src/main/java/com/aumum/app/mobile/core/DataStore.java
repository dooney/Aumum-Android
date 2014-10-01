package com.aumum.app.mobile.core;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;

/**
 * Created by Administrator on 30/09/2014.
 */
public class DataStore {
    private BootstrapService service;

    private DateTime lastUpdateTime;

    private int limitPerLoad = 25;

    public void setService(BootstrapService service) {
        this.service = service;
    }

    public List<Party> getListUpwards() {
        List<Party> partyList;
        if (lastUpdateTime != null) {
            partyList = service.getPartiesAfter(lastUpdateTime, Integer.MAX_VALUE);
        } else {
            partyList = service.getPartiesAfter(null, limitPerLoad);
        }
        lastUpdateTime = DateTime.now(DateTimeZone.UTC);
        return partyList;
    }

    public List<Party> getListBackwards(String time) {
        DateTime before = new DateTime(time, DateTimeZone.UTC);
        return service.getPartiesBefore(before, limitPerLoad);
    }
}
