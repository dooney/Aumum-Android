package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.MomentEntity;
import com.aumum.app.mobile.core.dao.gen.MomentEntityDao;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.MomentComment;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 2/03/2015.
 */
public class MomentStore {
    private RestService restService;
    private MomentEntityDao momentEntityDao;
    private Gson gson = new Gson();

    public static final int LIMIT_PER_LOAD = 15;

    public MomentStore(RestService restService, Repository repository) {
        this.restService = restService;
        this.momentEntityDao = repository.getMomentEntityDao();
    }

    private List<String> getList(String data) {
        if (data != null) {
            Gson gson = new Gson();
            return gson.fromJson(data, new TypeToken<List<String>>(){}.getType());
        }
        return null;
    }

    private List<Moment> map(List<MomentEntity> momentList) {
        List<Moment> result = new ArrayList<>();
        for (MomentEntity momentEntity: momentList) {
            Moment moment = map(momentEntity);
            result.add(moment);
        }
        return result;
    }

    private Moment map(MomentEntity momentEntity) {
        return new Moment(
                momentEntity.getObjectId(),
                DateUtils.dateToString(momentEntity.getCreatedAt(), Constants.DateTime.FORMAT),
                momentEntity.getUserId(),
                getList(momentEntity.getLikes()),
                momentEntity.getText(),
                momentEntity.getImageUrl());
    }

    private MomentEntity map(Moment moment) throws Exception {
        Date createdAt = DateUtils.stringToDate(moment.getCreatedAt(), Constants.DateTime.FORMAT);
        return new MomentEntity(
                moment.getObjectId(),
                createdAt,
                moment.getUserId(),
                gson.toJson(moment.getLikes()),
                moment.getText(),
                moment.getImageUrl());
    }

    public void updateOrInsert(List<Moment> momentList) throws Exception {
        for (Moment moment: momentList) {
            momentEntityDao.insertOrReplace(map(moment));
        }
    }

    public List<Moment> refresh(String time) throws Exception {
        try {
            int limit = time != null ? Integer.MAX_VALUE : LIMIT_PER_LOAD;
            List<Moment> momentList = restService.getMomentsAfter(time, limit);
            updateOrInsert(momentList);
            return momentList;
        } catch (Exception e) {
            QueryBuilder<MomentEntity> query = momentEntityDao.queryBuilder();
            if (time != null) {
                Date createdAt = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
                query = query.where(MomentEntityDao.Properties.CreatedAt.gt(createdAt));
            }
            List<MomentEntity> records = query
                    .orderDesc(MomentEntityDao.Properties.CreatedAt)
                    .limit(LIMIT_PER_LOAD)
                    .list();
            return map(records);
        }
    }

    public List<Moment> loadMore(String time) throws Exception {
        try {
            List<Moment> momentList = restService.getMomentsBefore(time, LIMIT_PER_LOAD);
            updateOrInsert(momentList);
            return momentList;
        } catch (Exception e) {
            Date date = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
            List<MomentEntity> records = momentEntityDao.queryBuilder()
                    .where(MomentEntityDao.Properties.CreatedAt.lt(date))
                    .orderDesc(MomentEntityDao.Properties.CreatedAt)
                    .limit(LIMIT_PER_LOAD)
                    .list();
            return map(records);
        }
    }

    public List<Moment> loadMore(List<String> idList, String time) throws Exception {
        try {
            List<Moment> momentList = restService.getMomentsBefore(idList, time, LIMIT_PER_LOAD);
            updateOrInsert(momentList);
            return momentList;
        } catch (Exception e) {
            Date date = new Date();
            if (time != null) {
                date = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
            }
            List<MomentEntity> records = momentEntityDao.queryBuilder()
                    .where(MomentEntityDao.Properties.ObjectId.in(idList))
                    .where(MomentEntityDao.Properties.CreatedAt.lt(date))
                    .orderDesc(MomentEntityDao.Properties.CreatedAt)
                    .limit(LIMIT_PER_LOAD)
                    .list();
            return map(records);
        }
    }

    public Moment getById(String momentId) {
        MomentEntity momentEntity = momentEntityDao.load(momentId);
        if (momentEntity != null) {
            return map(momentEntity);
        } else {
            return restService.getMomentById(momentId);
        }
    }

    public List<MomentComment> getComments(String momentId) {
        return null;
    }

    public void save(Moment moment) throws Exception {
        momentEntityDao.insertOrReplace(map(moment));
    }
}
