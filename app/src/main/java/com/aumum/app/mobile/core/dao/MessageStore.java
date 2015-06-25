package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.ContactRequestEntity;
import com.aumum.app.mobile.core.dao.entity.MomentCommentEntity;
import com.aumum.app.mobile.core.dao.entity.MomentLikeEntity;
import com.aumum.app.mobile.core.dao.gen.ContactRequestEntityDao;
import com.aumum.app.mobile.core.dao.gen.MomentCommentEntityDao;
import com.aumum.app.mobile.core.dao.gen.MomentLikeEntityDao;
import com.aumum.app.mobile.core.model.ContactRequest;
import com.aumum.app.mobile.core.model.MomentComment;
import com.aumum.app.mobile.core.model.MomentLike;
import com.aumum.app.mobile.utils.DateUtils;

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

    public static final int LIMIT_PER_LOAD = 12;

    public MessageStore(Repository repository) {
        this.contactRequestEntityDao = repository.getContactRequestEntityDao();
        this.momentLikeEntityDao = repository.getMomentLikeEntityDao();
        this.momentCommentEntityDao = repository.getMomentCommentEntityDao();
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

    private List<ContactRequest> mapContactRequests(List<ContactRequestEntity> entities) {
        List<ContactRequest> result = new ArrayList<>();
        for (ContactRequestEntity entity: entities) {
            String createdAt = DateUtils.dateToString(
                    entity.getCreatedAt(), Constants.DateTime.FORMAT);
            result.add(new ContactRequest(
                    entity.getUserId(),
                    entity.getInfo(),
                    createdAt));
        }
        return result;
    }

    public List<ContactRequest> getContactRequestsAfter(String after) throws Exception {
        QueryBuilder<ContactRequestEntity> query = contactRequestEntityDao.queryBuilder();
        if (after != null) {
            Date createdAt = DateUtils.stringToDate(after, Constants.DateTime.FORMAT);
            query = query.where(ContactRequestEntityDao.Properties.CreatedAt.gt(createdAt));
        }
        List<ContactRequestEntity> entities = query
                .orderDesc(ContactRequestEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        return mapContactRequests(entities);
    }

    public List<ContactRequest> getContactRequestsBefore(String before) throws Exception {
        Date date = DateUtils.stringToDate(before, Constants.DateTime.FORMAT);
        List<ContactRequestEntity> entities = contactRequestEntityDao.queryBuilder()
                .where(ContactRequestEntityDao.Properties.CreatedAt.lt(date))
                .orderDesc(ContactRequestEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        return mapContactRequests(entities);
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
            String createdAt = DateUtils.dateToString(
                    entity.getCreatedAt(), Constants.DateTime.FORMAT);
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

    private List<MomentComment> mapMomentComments(List<MomentCommentEntity> entities) {
        List<MomentComment> result = new ArrayList<>();
        for (MomentCommentEntity entity: entities) {
            String createdAt = DateUtils.dateToString(
                    entity.getCreatedAt(), Constants.DateTime.FORMAT);
            result.add(new MomentComment(
                    entity.getMomentId(),
                    entity.getUserId(),
                    createdAt,
                    entity.getContent()));
        }
        return result;
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

    public List<MomentComment> getMomentCommentsAfter(String after) throws Exception {
        QueryBuilder<MomentCommentEntity> query = momentCommentEntityDao.queryBuilder();
        if (after != null) {
            Date createdAt = DateUtils.stringToDate(after, Constants.DateTime.FORMAT);
            query = query.where(MomentLikeEntityDao.Properties.CreatedAt.gt(createdAt));
        }
        List<MomentCommentEntity> entities = query
                .orderDesc(MomentCommentEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        return mapMomentComments(entities);
    }

    public List<MomentComment> getMomentCommentsBefore(String before) throws Exception {
        Date date = DateUtils.stringToDate(before, Constants.DateTime.FORMAT);
        List<MomentCommentEntity> entities = momentCommentEntityDao.queryBuilder()
                .where(MomentCommentEntityDao.Properties.CreatedAt.lt(date))
                .orderDesc(MomentCommentEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        return mapMomentComments(entities);
    }

    public void resetMomentCommentsUnread() {
        List<MomentCommentEntity> entities = momentCommentEntityDao.queryBuilder()
                .where(MomentCommentEntityDao.Properties.IsRead.eq(false))
                .list();
        for (MomentCommentEntity entity: entities) {
            entity.setIsRead(true);
            momentCommentEntityDao.insertOrReplace(entity);
        }
    }
}
