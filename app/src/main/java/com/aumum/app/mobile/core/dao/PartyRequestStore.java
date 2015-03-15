package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.PartyRequestEntity;
import com.aumum.app.mobile.core.dao.gen.PartyRequestEntityDao;
import com.aumum.app.mobile.core.model.PartyRequest;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 13/03/2015.
 */
public class PartyRequestStore {

    private RestService restService;
    private PartyRequestEntityDao partyRequestEntityDao;

    public static final int LIMIT_PER_LOAD = 15;

    public PartyRequestStore(RestService restService, Repository repository) {
        this.restService = restService;
        this.partyRequestEntityDao = repository.getPartyRequestEntityDao();
    }

    private List<PartyRequest> map(List<PartyRequestEntity> partyRequestList) {
        List<PartyRequest> result = new ArrayList<PartyRequest>();
        for (PartyRequestEntity partyRequestEntity: partyRequestList) {
            PartyRequest partyRequest = map(partyRequestEntity);
            result.add(partyRequest);
        }
        return result;
    }

    private PartyRequest map(PartyRequestEntity partyRequestEntity) {
        return new PartyRequest(
                partyRequestEntity.getObjectId(),
                DateUtils.dateToString(partyRequestEntity.getCreatedAt(), Constants.DateTime.FORMAT),
                partyRequestEntity.getUserId(),
                partyRequestEntity.getCity(),
                partyRequestEntity.getArea(),
                partyRequestEntity.getType(),
                partyRequestEntity.getSubType());
    }

    private PartyRequestEntity map(PartyRequest partyRequest) throws Exception {
        Date createdAt = DateUtils.stringToDate(partyRequest.getCreatedAt(), Constants.DateTime.FORMAT);
        return new PartyRequestEntity(
                partyRequest.getObjectId(),
                createdAt,
                partyRequest.getUserId(),
                partyRequest.getCity(),
                partyRequest.getArea(),
                partyRequest.getType(),
                partyRequest.getSubType()
        );
    }

    public void updateOrInsert(List<PartyRequest> partyRequestList) throws Exception {
        for (PartyRequest partyRequest: partyRequestList) {
            partyRequestEntityDao.insertOrReplace(map(partyRequest));
        }
    }

    public List<PartyRequest> getUpwardsList(String time) throws Exception {
        QueryBuilder<PartyRequestEntity> query = partyRequestEntityDao.queryBuilder();
        if (time != null) {
            Date createdAt = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
            query = query.where(PartyRequestEntityDao.Properties.CreatedAt.gt(createdAt));
        }
        List<PartyRequestEntity> records = query
                .orderDesc(PartyRequestEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        if (records.size() > 0) {
            return map(records);
        } else {
            int limit = time != null ? Integer.MAX_VALUE : LIMIT_PER_LOAD;
            List<PartyRequest> partyRequestList = restService.getPartyRequestsAfter(time, limit);
            updateOrInsert(partyRequestList);
            return partyRequestList;
        }
    }

    public List<PartyRequest> getBackwardsList(String time) throws Exception {
        return restService.getPartyRequestsBefore(time, LIMIT_PER_LOAD);
    }

    public int getUnreadCount() throws Exception {
        String time = getLastUpdateTime();
        return restService.getPartyRequestsCountAfter(time);
    }

    private String getLastUpdateTime() {
        PartyRequestEntity partyRequestEntity = partyRequestEntityDao.queryBuilder()
                .orderDesc(PartyRequestEntityDao.Properties.CreatedAt)
                .limit(1)
                .unique();
        if (partyRequestEntity != null) {
            return DateUtils.dateToString(partyRequestEntity.getCreatedAt(), Constants.DateTime.FORMAT);
        }
        return null;
    }
}
