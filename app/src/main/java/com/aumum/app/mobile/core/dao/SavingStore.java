package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.SavingEntity;
import com.aumum.app.mobile.core.dao.gen.SavingEntityDao;
import com.aumum.app.mobile.core.model.Saving;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 12/03/2015.
 */
public class SavingStore {
    private RestService restService;
    private SavingEntityDao savingEntityDao;
    private Gson gson = new Gson();

    public static final int LIMIT_PER_LOAD = 15;

    public SavingStore(RestService restService, Repository repository) {
        this.restService = restService;
        this.savingEntityDao = repository.getSavingEntityDao();
    }

    private List<String> getList(String data) {
        if (data != null) {
            Gson gson = new Gson();
            return gson.fromJson(data, new TypeToken<List<String>>(){}.getType());
        }
        return null;
    }

    private List<Saving> map(List<SavingEntity> savingList) {
        List<Saving> result = new ArrayList<Saving>();
        for (SavingEntity savingEntity: savingList) {
            Saving saving = map(savingEntity);
            result.add(saving);
        }
        return result;
    }

    private Saving map(SavingEntity savingEntity) {
        return new Saving(
                savingEntity.getObjectId(),
                DateUtils.dateToString(savingEntity.getCreatedAt(), Constants.DateTime.FORMAT),
                savingEntity.getUserId(),
                savingEntity.getAmount(),
                getList(savingEntity.getImages()),
                getList(savingEntity.getLikes()),
                getList(savingEntity.getComments()));
    }

    private SavingEntity map(Saving saving) throws Exception {
        Date createdAt = DateUtils.stringToDate(saving.getCreatedAt(), Constants.DateTime.FORMAT);
        return new SavingEntity(
                saving.getObjectId(),
                createdAt,
                saving.getUserId(),
                saving.getAmount(),
                gson.toJson(saving.getImages()),
                gson.toJson(saving.getLikes()),
                gson.toJson(saving.getComments()));
    }

    public void updateOrInsert(List<Saving> savingList) throws Exception {
        for (Saving saving: savingList) {
            savingEntityDao.insertOrReplace(map(saving));
        }
    }

    public List<Saving> getUpwardsList(String time) throws Exception {
        QueryBuilder<SavingEntity> query = savingEntityDao.queryBuilder();
        if (time != null) {
            Date createdAt = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
            query = query.where(SavingEntityDao.Properties.CreatedAt.gt(createdAt));
        }
        List<SavingEntity> records = query
                .orderDesc(SavingEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        if (records.size() > 0) {
            return map(records);
        } else {
            int limit = time != null ? Integer.MAX_VALUE : LIMIT_PER_LOAD;
            List<Saving> savingList = restService.getSavingsAfter(time, limit);
            updateOrInsert(savingList);
            return savingList;
        }
    }

    public List<Saving> getUpwardsList(List<String> idList) throws Exception {
        List<Saving> savingList = restService.getSavings(idList, LIMIT_PER_LOAD);
        for (Saving saving: savingList) {
            savingEntityDao.insertOrReplace(map(saving));
        }
        return savingList;
    }

    public List<Saving> getBackwardsList(String time) throws Exception {
        Date date = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
        List<SavingEntity> records = savingEntityDao.queryBuilder()
                .where(SavingEntityDao.Properties.CreatedAt.lt(date))
                .orderDesc(SavingEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        if (records.size() > 0) {
            return map(records);
        } else {
            List<Saving> savingList = restService.getSavingsBefore(time, LIMIT_PER_LOAD);
            updateOrInsert(savingList);
            return savingList;
        }
    }

    public List<Saving> getBackwardsList(List<String> idList, String time) throws Exception {
        Date date = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
        List<SavingEntity> records = savingEntityDao.queryBuilder()
                .where(SavingEntityDao.Properties.ObjectId.in(idList))
                .where(SavingEntityDao.Properties.CreatedAt.lt(date))
                .orderDesc(SavingEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        if (records.size() > 0) {
            return map(records);
        } else {
            List<Saving> savingList = restService.getSavingsBefore(idList, time, LIMIT_PER_LOAD);
            updateOrInsert(savingList);
            return savingList;
        }
    }

    public Saving getSavingByIdFromServer(String id) throws Exception {
        Saving saving = restService.getSavingById(id);
        if (saving.getDeletedAt() == null) {
            savingEntityDao.insertOrReplace(map(saving));
        }
        return saving;
    }

    public Saving getSavingById(String id) throws Exception {
        SavingEntity savingEntity = savingEntityDao.load(id);
        if (savingEntity != null) {
            return map(savingEntity);
        }
        return null;
    }

    public int getUnreadCount() throws Exception {
        String time = getLastUpdateTime();
        return restService.getSavingsCountAfter(time);
    }

    private String getLastUpdateTime() {
        SavingEntity savingEntity = savingEntityDao.queryBuilder()
                .orderDesc(SavingEntityDao.Properties.CreatedAt)
                .limit(1)
                .unique();
        if (savingEntity != null) {
            return DateUtils.dateToString(savingEntity.getCreatedAt(), Constants.DateTime.FORMAT);
        }
        return null;
    }

    public void deleteSaving(String savingId) {
        savingEntityDao.deleteByKey(savingId);
    }

    public void save(Saving saving) throws Exception {
        savingEntityDao.insertOrReplace(map(saving));
    }
}
