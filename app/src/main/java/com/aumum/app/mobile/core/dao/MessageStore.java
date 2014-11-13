package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.vm.MessageVM;
import com.aumum.app.mobile.core.dao.gen.MessageVMDao;
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
    private MessageVMDao messageVMDao;

    private final int LIMIT_PER_LOAD = 10;

    public MessageStore(RestService restService, Repository repository) {
        this.restService = restService;
        this.messageVMDao = repository.getMessageVMDao();
    }

    private MessageVM map(Message message) throws Exception {
        Date createdAt = DateUtils.stringToDate(message.getCreatedAt(), Constants.DateTime.FORMAT);
        return new MessageVM(null, message.getObjectId(), createdAt, message.getFromUserId(),
                message.getToUserId(), message.getType(), message.getContent(), message.getParent());
    }

    private List<MessageVM> map(List<Message> messageList) throws Exception {
        List<MessageVM> result = new ArrayList<MessageVM>();
        for (Message message: messageList) {
            MessageVM messageVM = map(message);
            result.add(messageVM);
            messageVMDao.insertOrReplace(messageVM);
        }
        return result;
    }

    public List<MessageVM> getUpwardsList(List<String> idList, int[] typeList, String time) throws Exception {
        if (time != null) {
            DateTime after = new DateTime(time, DateTimeZone.UTC);
            List<Message> messageList = restService.getMessagesAfter(idList, typeList, after, Integer.MAX_VALUE);
            return map(messageList);
        } else {
            List<MessageVM> records = messageVMDao.queryBuilder()
                    .orderDesc(MessageVMDao.Properties.CreatedAt)
                    .limit(LIMIT_PER_LOAD)
                    .list();
            if (records.size() > 0) {
                return records;
            } else {
                List<Message> messageList = restService.getMessagesAfter(idList, typeList, null, LIMIT_PER_LOAD);
                return map(messageList);
            }
        }
    }

    public List<MessageVM> getBackwardsList(List<String> idList, int[] typeList, String time) throws Exception {
        Date date = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
        ArrayList<Integer> types = new ArrayList<Integer>();
        for (int type: typeList) {
            types.add(type);
        }
        List<MessageVM> records = messageVMDao.queryBuilder()
                .where(MessageVMDao.Properties.ObjectId.in(idList))
                .where(MessageVMDao.Properties.Type.in(types))
                .where(MessageVMDao.Properties.CreatedAt.lt(date))
                .orderDesc(MessageVMDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        if (records.size() > 0) {
            return records;
        } else {
            DateTime before = new DateTime(time, DateTimeZone.UTC);
            List<Message> messageList = restService.getMessagesBefore(idList, typeList, before, LIMIT_PER_LOAD);
            return map(messageList);
        }
    }

    public void remove(MessageVM message) {
        messageVMDao.deleteByKey(message.getId());
    }
}