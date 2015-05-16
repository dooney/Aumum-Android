package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.ContactRequestEntity;
import com.aumum.app.mobile.core.dao.entity.GroupRequestEntity;
import com.aumum.app.mobile.core.dao.gen.ContactRequestEntityDao;
import com.aumum.app.mobile.core.dao.gen.GroupRequestEntityDao;
import com.aumum.app.mobile.core.model.ContactRequest;
import com.aumum.app.mobile.core.model.GroupRequest;
import com.aumum.app.mobile.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 16/05/2015.
 */
public class MessageStore {

    private ContactRequestEntityDao contactRequestEntityDao;
    private GroupRequestEntityDao groupRequestEntityDao;

    public MessageStore(Repository repository) {
        this.contactRequestEntityDao = repository.getContactRequestEntityDao();
        this.groupRequestEntityDao = repository.getGroupRequestEntityDao();
    }

    public void addContactRequest(String userId, String info) {
        Date now = new Date();
        ContactRequestEntity contactRequestEntity =
                new ContactRequestEntity(userId, now, info, false);
        contactRequestEntityDao.insertOrReplace(contactRequestEntity);
    }

    public void deleteContactRequest(String userId) {
        contactRequestEntityDao.deleteByKey(userId);
    }

    public List<ContactRequest> getContactRequestList() throws Exception {
        List<ContactRequestEntity> entities = contactRequestEntityDao.queryBuilder()
                .orderDesc(ContactRequestEntityDao.Properties.CreatedAt)
                .list();
        List<ContactRequest> result = new ArrayList<ContactRequest>();
        for (ContactRequestEntity entity: entities) {
            String createdAt = DateUtils.dateToString(
                    entity.getCreatedAt(), Constants.DateTime.FORMAT);
            result.add(new ContactRequest(
                    entity.getUserId(), entity.getInfo(), createdAt));
        }
        return result;
    }

    public boolean hasContactRequest(String userId) {
        return contactRequestEntityDao.load(userId) != null;
    }

    public boolean hasContactRequestsUnread() {
        return contactRequestEntityDao.queryBuilder()
                .where(ContactRequestEntityDao.Properties.IsRead.eq(false))
                .count() > 0;
    }

    public void resetContactRequestsUnread() {
        List<ContactRequestEntity> entities = contactRequestEntityDao.queryBuilder()
                .where(ContactRequestEntityDao.Properties.IsRead.eq(false))
                .list();
        for (ContactRequestEntity entity: entities) {
            entity.setIsRead(true);
            contactRequestEntityDao.insertOrReplace(entity);
        }
    }

    public void addGroupRequest(String groupId,
                                String userId,
                                String info,
                                int status) {
        Date now = new Date();
        GroupRequestEntity groupRequestEntity = new GroupRequestEntity(
                groupId, userId, now, info, status, false);
        groupRequestEntityDao.insert(groupRequestEntity);
    }

    public List<GroupRequest> getGroupRequestList() throws Exception {
        List<GroupRequestEntity> entities = groupRequestEntityDao.queryBuilder()
                .orderDesc(GroupRequestEntityDao.Properties.CreatedAt)
                .list();
        List<GroupRequest> result = new ArrayList<>();
        for (GroupRequestEntity entity: entities) {
            String createdAt = DateUtils.dateToString(
                    entity.getCreatedAt(), Constants.DateTime.FORMAT);
            result.add(new GroupRequest(
                    entity.getGroupId(),
                    entity.getUserId(),
                    createdAt,
                    entity.getInfo(),
                    entity.getStatus()));
        }
        return result;
    }

    public void saveGroupRequest(GroupRequest request) throws Exception {
        Date createdAt = DateUtils.stringToDate(
                request.getCreatedAt(), Constants.DateTime.FORMAT);
        GroupRequestEntity entity = new GroupRequestEntity(
                request.getGroupId(),
                request.getUserId(),
                createdAt,
                request.getInfo(),
                request.getStatus(),
                true);
        groupRequestEntityDao.insertOrReplace(entity);
    }
}
