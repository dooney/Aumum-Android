package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.MessageEntity;
import com.aumum.app.mobile.core.dao.gen.MessageEntityDao;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 7/10/2014.
 */
public class MessageStore {
    private RestService restService;
    private MessageEntityDao messageEntityDao;
    private List<Message> unreadList = new ArrayList<Message>();

    private final int LIMIT_PER_LOAD = 10;

    public MessageStore(RestService restService, Repository repository) {
        this.restService = restService;
        this.messageEntityDao = repository.getMessageEntityDao();
    }

    public List<Message> getUnreadList() {
        return unreadList;
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

    private MessageEntity map(Message message) throws Exception {
        Date createdAt = DateUtils.stringToDate(message.getCreatedAt(), Constants.DateTime.FORMAT);
        return new MessageEntity(
                message.getObjectId(),
                createdAt,
                message.getFromUserId(),
                message.getToUserId(),
                message.getType(),
                message.getContent(),
                message.getParent());
    }

    public List<Message> getUpwardsList(List<String> idList, List<Integer> typeList, String time) throws Exception {
        QueryBuilder<MessageEntity> query = messageEntityDao.queryBuilder()
                .where(MessageEntityDao.Properties.ObjectId.in(idList));
        if (typeList != null) {
            query = query.where(MessageEntityDao.Properties.Type.in(typeList));
        }
        if (time != null) {
            Date createdAt = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
            query = query.where(MessageEntityDao.Properties.CreatedAt.gt(createdAt));
        }
        List<MessageEntity> records = query
                .orderDesc(MessageEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        if (records.size() > 0) {
            return map(records);
        } else {
            int limit = time != null ? Integer.MAX_VALUE : LIMIT_PER_LOAD;
            List<Message> messageList = restService.getMessagesAfter(idList, typeList, time, limit);
            for (Message message: messageList) {
                messageEntityDao.insertOrReplace(map(message));
            }
            return messageList;
        }
    }

    public List<Message> getBackwardsList(List<String> idList, List<Integer> typeList, String time) throws Exception {
        Date date = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
        List<MessageEntity> records = messageEntityDao.queryBuilder()
                .where(MessageEntityDao.Properties.ObjectId.in(idList))
                .where(MessageEntityDao.Properties.Type.in(typeList))
                .where(MessageEntityDao.Properties.CreatedAt.lt(date))
                .orderDesc(MessageEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        if (records.size() > 0) {
            return map(records);
        } else {
            List<Message> messageList = restService.getMessagesBefore(idList, typeList, time, LIMIT_PER_LOAD);
            for (Message message: messageList) {
                messageEntityDao.insertOrReplace(map(message));
            }
            return messageList;
        }
    }

    public void remove(Message message) {
        MessageEntity messageEntity = messageEntityDao.load(message.getObjectId());
        if (messageEntity != null) {
            messageEntityDao.deleteByKey(messageEntity.getObjectId());
        }
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
        }
        return null;
    }

    public void markAsRead(List<Integer> typeList) {
        for (Iterator<Message> it = unreadList.iterator(); it.hasNext();) {
            Message unread = it.next();
            if (typeList.contains(unread.getType())) {
                it.remove();
            }
        }
    }
}