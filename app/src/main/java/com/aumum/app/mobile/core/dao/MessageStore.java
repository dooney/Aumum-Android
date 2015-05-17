package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.ContactRequestEntity;
import com.aumum.app.mobile.core.dao.entity.GroupRequestEntity;
import com.aumum.app.mobile.core.dao.entity.MomentCommentEntity;
import com.aumum.app.mobile.core.dao.entity.MomentLikeEntity;
import com.aumum.app.mobile.core.dao.gen.ContactRequestEntityDao;
import com.aumum.app.mobile.core.dao.gen.GroupRequestEntityDao;
import com.aumum.app.mobile.core.dao.gen.MomentCommentEntityDao;
import com.aumum.app.mobile.core.dao.gen.MomentLikeEntityDao;
import com.aumum.app.mobile.core.model.ContactRequest;
import com.aumum.app.mobile.core.model.GroupRequest;
import com.aumum.app.mobile.core.model.MomentLike;
import com.aumum.app.mobile.utils.DateUtils;
import com.aumum.app.mobile.utils.TimeUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 16/05/2015.
 */
public class MessageStore {

    private ContactRequestEntityDao contactRequestEntityDao;
    private MomentLikeEntityDao momentLikeEntityDao;
    private MomentCommentEntityDao momentCommentEntityDao;
    private GroupRequestEntityDao groupRequestEntityDao;

    public static final int LIMIT_PER_LOAD = 15;

    public MessageStore(Repository repository) {
        this.contactRequestEntityDao = repository.getContactRequestEntityDao();
        this.momentLikeEntityDao = repository.getMomentLikeEntityDao();
        this.momentCommentEntityDao = repository.getMomentCommentEntityDao();
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
            result.add(new ContactRequest(
                    entity.getUserId(), entity.getInfo()));
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

    private List<MomentLike> mapMomentLikes(List<MomentLikeEntity> entities) {
        List<MomentLike> result = new ArrayList<>();
        for (MomentLikeEntity entity: entities) {
            String createdAt = TimeUtils.getFormattedTimeString(
                    new DateTime(entity.getCreatedAt()));
            result.add(new MomentLike(
                    entity.getMomentId(),
                    entity.getUserId(),
                    createdAt));
        }
        return result;
    }

    public void addMomentLike(String momentId, String userId) {
        Date now = new Date();
        MomentLikeEntity momentLikeEntity = new MomentLikeEntity(
                null, userId, now, momentId, false);
        momentLikeEntityDao.insertOrReplace(momentLikeEntity);
    }

    public boolean hasMomentLikesUnread() {
        return momentLikeEntityDao.queryBuilder()
                .where(MomentLikeEntityDao.Properties.IsRead.eq(false))
                .count() > 0;
    }

    public List<MomentLike> getMomentLikesAfter(String after) throws Exception {
        QueryBuilder<MomentLikeEntity> query = momentLikeEntityDao.queryBuilder();
        if (after != null) {
            Date createdAt = DateUtils.stringToDate(after, Constants.DateTime.FORMAT);
            query = query.where(MomentLikeEntityDao.Properties.CreatedAt.gt(createdAt));
        }
        List<MomentLikeEntity> entities = query
                .orderDesc(MomentLikeEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        return mapMomentLikes(entities);
    }

    public List<MomentLike> getMomentLikesBefore(String before) throws Exception {
        Date date = DateUtils.stringToDate(before, Constants.DateTime.FORMAT);
        List<MomentLikeEntity> entities = momentLikeEntityDao.queryBuilder()
                .where(MomentLikeEntityDao.Properties.CreatedAt.lt(date))
                .orderDesc(MomentLikeEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        return mapMomentLikes(entities);
    }

    public void resetMomentLikesUnread() {
        List<MomentLikeEntity> entities = momentLikeEntityDao.queryBuilder()
                .where(MomentLikeEntityDao.Properties.IsRead.eq(false))
                .list();
        for (MomentLikeEntity entity: entities) {
            entity.setIsRead(true);
            momentLikeEntityDao.insertOrReplace(entity);
        }
    }

    public void addMomentComment(String momentId, String userId, String comment) {
        Date now = new Date();
        MomentCommentEntity momentCommentEntity = new MomentCommentEntity(
                null, userId, now, momentId, comment, false);
        momentCommentEntityDao.insertOrReplace(momentCommentEntity);
    }

    public boolean hasMomentCommentsUnread() {
        return momentCommentEntityDao.queryBuilder()
                .where(MomentCommentEntityDao.Properties.IsRead.eq(false))
                .count() > 0;
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
            result.add(new GroupRequest(
                    entity.getGroupId(),
                    entity.getUserId(),
                    entity.getInfo(),
                    entity.getStatus()));
        }
        return result;
    }

    public void saveGroupRequest(GroupRequest request) throws Exception {
        GroupRequestEntity entity = groupRequestEntityDao.load(request.getUserId());
        entity.setStatus(request.getStatus());
        entity.setIsRead(true);
        groupRequestEntityDao.insertOrReplace(entity);
    }
}
