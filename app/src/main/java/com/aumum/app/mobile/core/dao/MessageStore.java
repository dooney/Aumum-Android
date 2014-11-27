package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.MessageEntity;
import com.aumum.app.mobile.core.dao.gen.MessageEntityDao;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 7/10/2014.
 */
public class MessageStore {
    private RestService restService;
    private MessageEntityDao messageEntityDao;
    private List<Message> unreadList = new ArrayList<Message>();

    public MessageStore(RestService restService, Repository repository) {
        this.restService = restService;
        this.messageEntityDao = repository.getMessageEntityDao();
    }

    public List<Message> getUnreadList() {
        return unreadList;
    }

    private MessageEntity map(Message message) throws Exception {
        Date createdAt = DateUtils.stringToDate(message.getCreatedAt(), Constants.DateTime.FORMAT);
        return new MessageEntity(message.getObjectId(), createdAt);
    }

    public List<Message> getUnreadListFromServer(List<String> idList) throws Exception {
        String time = getLastUpdateTime();
        List<Message> messageList = restService.getMessagesAfter(idList, null, time, Integer.MAX_VALUE);
        for (Message message: messageList) {
            messageEntityDao.insertOrReplace(map(message));
        }
        return messageList;
    }

    private String getLastUpdateTime() {
        MessageEntity messageEntity = messageEntityDao.queryBuilder()
                .orderDesc(MessageEntityDao.Properties.CreatedAt)
                .limit(1)
                .unique();
        if (messageEntity != null) {
            return DateUtils.dateToString(messageEntity.getCreatedAt(), Constants.DateTime.FORMAT);
        } else {
            DateTime now = DateTime.now(DateTimeZone.UTC);
            return now.toString(Constants.DateTime.FORMAT);
        }
    }
}