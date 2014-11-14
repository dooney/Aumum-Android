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

    private final int LIMIT_PER_LOAD = 10;

    public MessageStore(RestService restService, Repository repository) {
        this.restService = restService;
        this.messageEntityDao = repository.getMessageEntityDao();
    }

    private List<Message> map(List<MessageEntity> partyList) {
        List<Message> result = new ArrayList<Message>();
        for (MessageEntity messageEntity: partyList) {
            Message message = map(messageEntity);
            result.add(message);
        }
        return result;
    }

    private Message map(MessageEntity messageEntity) {
        String createdAt = DateUtils.dateToString(messageEntity.getCreatedAt(), Constants.DateTime.FORMAT);
        return new Message(
                messageEntity.getObjectId(),
                createdAt,
                messageEntity.getType(),
                messageEntity.getFromUserId(),
                messageEntity.getToUserId(),
                messageEntity.getContent(),
                messageEntity.getParent());
    }

    private MessageEntity map(Message message, Long pk) throws Exception {
        Date createdAt = DateUtils.stringToDate(message.getCreatedAt(), Constants.DateTime.FORMAT);
        return new MessageEntity(
                pk,
                message.getObjectId(),
                createdAt,
                message.getFromUserId(),
                message.getToUserId(),
                message.getType(),
                message.getContent(),
                message.getParent());
    }

    private void updateOrInsert(List<Message> messageList) throws Exception {
        for (Message message: messageList) {
            updateOrInsert(message);
        }
    }

    private void updateOrInsert(Message message) throws Exception {
        MessageEntity messageEntity = messageEntityDao.queryBuilder()
                .where(MessageEntityDao.Properties.ObjectId.eq(message.getObjectId()))
                .unique();
        Long pk = messageEntity != null ? messageEntity.getId() : null;
        messageEntity = map(message, pk);
        messageEntityDao.insertOrReplace(messageEntity);
    }

    public List<Message> getUpwardsList(List<String> idList, int[] typeList, String time) throws Exception {
        if (time != null) {
            DateTime after = new DateTime(time, DateTimeZone.UTC);
            List<Message> messageList = restService.getMessagesAfter(idList, typeList, after, Integer.MAX_VALUE);
            updateOrInsert(messageList);
            return messageList;
        } else {
            ArrayList<Integer> types = new ArrayList<Integer>();
            for (int type: typeList) {
                types.add(type);
            }
            List<MessageEntity> records = messageEntityDao.queryBuilder()
                    .where(MessageEntityDao.Properties.ObjectId.in(idList))
                    .where(MessageEntityDao.Properties.Type.in(types))
                    .orderDesc(MessageEntityDao.Properties.CreatedAt)
                    .limit(LIMIT_PER_LOAD)
                    .list();
            if (records.size() > 0) {
                return map(records);
            } else {
                List<Message> messageList = restService.getMessagesAfter(idList, typeList, null, LIMIT_PER_LOAD);
                updateOrInsert(messageList);
                return messageList;
            }
        }
    }

    public List<Message> getBackwardsList(List<String> idList, int[] typeList, String time) throws Exception {
        Date date = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
        ArrayList<Integer> types = new ArrayList<Integer>();
        for (int type: typeList) {
            types.add(type);
        }
        List<MessageEntity> records = messageEntityDao.queryBuilder()
                .where(MessageEntityDao.Properties.ObjectId.in(idList))
                .where(MessageEntityDao.Properties.Type.in(types))
                .where(MessageEntityDao.Properties.CreatedAt.lt(date))
                .orderDesc(MessageEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        if (records.size() > 0) {
            return map(records);
        } else {
            DateTime before = new DateTime(time, DateTimeZone.UTC);
            List<Message> messageList = restService.getMessagesBefore(idList, typeList, before, LIMIT_PER_LOAD);
            updateOrInsert(messageList);
            return messageList;
        }
    }

    public void remove(Message message) {
        MessageEntity messageEntity = messageEntityDao.queryBuilder()
                .where(MessageEntityDao.Properties.ObjectId.eq(message.getObjectId()))
                .unique();
        if (messageEntity != null) {
            messageEntityDao.deleteByKey(messageEntity.getId());
        }
    }
}