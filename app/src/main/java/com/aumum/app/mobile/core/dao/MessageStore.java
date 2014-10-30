package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.service.RestService;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 7/10/2014.
 */
public class MessageStore {
    @Inject RestService restService;

    private final int LIMIT_PER_LOAD = 10;

    public MessageStore() {
        Injector.inject(this);
    }

    public List<Message> getUpwardsList(List<String> idList, int[] typeList, String time) {
        List<Message> messageList;
        if (time != null) {
            DateTime after = new DateTime(time, DateTimeZone.UTC);
            messageList = restService.getMessagesAfter(idList, typeList, after, Integer.MAX_VALUE);
        } else {
            messageList = restService.getMessagesAfter(idList, typeList, null, LIMIT_PER_LOAD);
        }
        return messageList;
    }

    public List<Message> getBackwardsList(List<String> idList, int[] typeList, String time) {
        DateTime before = new DateTime(time, DateTimeZone.UTC);
        return restService.getMessagesBefore(idList, typeList, before, LIMIT_PER_LOAD);
    }
}