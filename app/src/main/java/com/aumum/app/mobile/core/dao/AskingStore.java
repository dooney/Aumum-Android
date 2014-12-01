package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.AskingEntity;
import com.aumum.app.mobile.core.dao.gen.AskingEntityDao;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 27/11/2014.
 */
public class AskingStore {

    private RestService restService;
    private AskingEntityDao askingEntityDao;
    private Gson gson = new Gson();

    private int LIMIT_PER_LOAD = 15;

    public AskingStore(RestService restService, Repository repository) {
        this.restService = restService;
        this.askingEntityDao = repository.getAskingEntityDao();
    }

    private List<String> getList(String data) {
        if (data != null) {
            Gson gson = new Gson();
            return gson.fromJson(data, new TypeToken<List<String>>(){}.getType());
        }
        return null;
    }

    private List<Asking> map(List<AskingEntity> askingList) {
        List<Asking> result = new ArrayList<Asking>();
        for (AskingEntity askingEntity: askingList) {
            Asking asking = map(askingEntity);
            result.add(asking);
        }
        return result;
    }

    private Asking map(AskingEntity askingEntity) {
        return new Asking(
                askingEntity.getObjectId(),
                DateUtils.dateToString(askingEntity.getCreatedAt(), Constants.DateTime.FORMAT),
                askingEntity.getUserId(),
                askingEntity.getCategory(),
                askingEntity.getQuestion(),
                getList(askingEntity.getReplies()));
    }

    private AskingEntity map(Asking asking) throws Exception {
        Date createdAt = DateUtils.stringToDate(asking.getCreatedAt(), Constants.DateTime.FORMAT);
        return new AskingEntity(
                asking.getObjectId(),
                createdAt,
                asking.getUserId(),
                asking.getCategory(),
                asking.getQuestion(),
                gson.toJson(asking.getReplies()));
    }

    private void updateOrInsert(List<Asking> askingList) throws Exception {
        for (Asking asking: askingList) {
            updateOrInsert(asking);
        }
    }

    private void updateOrInsert(Asking asking) throws Exception {
        askingEntityDao.insertOrReplace(map(asking));
    }

    public List<Asking> getUpwardsList(int category, String time) throws Exception {
        QueryBuilder<AskingEntity> query = askingEntityDao.queryBuilder();
        if (time != null) {
            Date createdAt = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
            query = query.where(AskingEntityDao.Properties.CreatedAt.gt(createdAt));
        }
        List<AskingEntity> records = query
                .where(AskingEntityDao.Properties.Category.eq(category))
                .orderDesc(AskingEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        if (records.size() > 0) {
            return map(records);
        } else {
            int limit = time != null ? Integer.MAX_VALUE : LIMIT_PER_LOAD;
            List<Asking> askingList = restService.getAskingListAfter(category, time, limit);
            updateOrInsert(askingList);
            return askingList;
        }
    }

    public Asking getAskingByIdFromServer(String id) throws Exception {
        Asking asking = restService.getAskingById(id);
        updateOrInsert(asking);
        return asking;
    }

    public Asking getAskingById(String id) {
        AskingEntity askingEntity = askingEntityDao.load(id);
        if (askingEntity != null) {
            return map(askingEntity);
        }
        return null;
    }

    public List<Asking> getList(List<String> idList) throws Exception {
        List<Asking> askingList = restService.getAskingList(idList);
        for (Asking asking: askingList) {
            askingEntityDao.insertOrReplace(map(asking));
        }
        return askingList;
    }
}
