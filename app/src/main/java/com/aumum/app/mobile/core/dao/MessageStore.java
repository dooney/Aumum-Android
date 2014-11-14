package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.MessageEntity;
import com.aumum.app.mobile.core.dao.gen.MessageEntityDao;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 7/10/2014.
 */
public class MessageStore {
    private RestService restService;
    private ApiKeyProvider apiKeyProvider;
    private MessageEntityDao messageEntityDao;

    private final int LIMIT_PER_LOAD = 10;

    public MessageStore(RestService restService, ApiKeyProvider apiKeyProvider, Repository repository) {
        this.restService = restService;
        this.apiKeyProvider = apiKeyProvider;
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
        String instanceId = apiKeyProvider.getAuthUserId();
        Date createdAt = DateUtils.stringToDate(message.getCreatedAt(), Constants.DateTime.FORMAT);
        return new MessageEntity(
                pk,
                instanceId,
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
        String instanceId = apiKeyProvider.getAuthUserId();
        MessageEntity messageEntity = messageEntityDao.queryBuilder()
                .where(MessageEntityDao.Properties.InstanceId.eq(instanceId))
                .where(MessageEntityDao.Properties.ObjectId.eq(message.getObjectId()))
                .unique();
        Long pk = messageEntity != null ? messageEntity.getId() : null;
        messageEntity = map(message, pk);
        messageEntityDao.insertOrReplace(messageEntity);
    }

    public List<Message> getUpwardsList(List<String> idList, int[] typeList, String time) throws Exception {
        if (time != null) {
            List<Message> messageList = restService.getMessagesAfter(idList, typeList, time, Integer.MAX_VALUE);
            updateOrInsert(messageList);
            return messageList;
        } else {
            ArrayList<Integer> types = new ArrayList<Integer>();
            for (int type: typeList) {
                types.add(type);
            }
            String instanceId = apiKeyProvider.getAuthUserId();
            List<MessageEntity> records = messageEntityDao.queryBuilder()
                    .where(MessageEntityDao.Properties.InstanceId.eq(instanceId))
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
        String instanceId = apiKeyProvider.getAuthUserId();
        Date date = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
        ArrayList<Integer> types = new ArrayList<Integer>();
        for (int type: typeList) {
            types.add(type);
        }
        List<MessageEntity> records = messageEntityDao.queryBuilder()
                .where(MessageEntityDao.Properties.InstanceId.eq(instanceId))
                .where(MessageEntityDao.Properties.ObjectId.in(idList))
                .where(MessageEntityDao.Properties.Type.in(types))
                .where(MessageEntityDao.Properties.CreatedAt.lt(date))
                .orderDesc(MessageEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        if (records.size() > 0) {
            return map(records);
        } else {
            List<Message> messageList = restService.getMessagesBefore(idList, typeList, time, LIMIT_PER_LOAD);
            updateOrInsert(messageList);
            return messageList;
        }
    }

    public void remove(Message message) {
        String instanceId = apiKeyProvider.getAuthUserId();
        MessageEntity messageEntity = messageEntityDao.queryBuilder()
                .where(MessageEntityDao.Properties.InstanceId.eq(instanceId))
                .where(MessageEntityDao.Properties.ObjectId.eq(message.getObjectId()))
                .unique();
        if (messageEntity != null) {
            messageEntityDao.deleteByKey(messageEntity.getId());
        }
    }

    private int getUnreadMessageCount(int[] typeList) {
        ArrayList<Integer> types = new ArrayList<Integer>();
        for (int type: typeList) {
            types.add(type);
        }
        String currentUserId = apiKeyProvider.getAuthUserId();
        MessageEntity record = messageEntityDao.queryBuilder()
                .where(MessageEntityDao.Properties.InstanceId.eq(currentUserId))
                .where(MessageEntityDao.Properties.Type.in(types))
                .orderDesc(MessageEntityDao.Properties.CreatedAt)
                .limit(1)
                .unique();
        String time = DateUtils.dateToString(record.getCreatedAt(), Constants.DateTime.FORMAT);
        return restService.getMessagesCountAfter(currentUserId, typeList, time);
    }

    public int getUnreadPartyMembershipCount() {
        return getUnreadMessageCount(Message.getSubCategoryTypes(Message.SubCategory.PARTY_MEMBERSHIP));
    }

    public int getUnreadPartyCommentsCount() {
        return getUnreadMessageCount(Message.getSubCategoryTypes(Message.SubCategory.PARTY_COMMENTS));
    }

    public int getUnreadPartyLikesCount() {
        return getUnreadMessageCount(Message.getSubCategoryTypes(Message.SubCategory.PARTY_LIKES));
    }
}