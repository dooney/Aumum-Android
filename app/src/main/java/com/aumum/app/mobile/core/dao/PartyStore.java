package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.PartyEntity;
import com.aumum.app.mobile.core.dao.gen.PartyEntityDao;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.PlaceRange;
import com.aumum.app.mobile.core.model.Time;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 30/09/2014.
 */
public class PartyStore {
    private RestService restService;
    private PartyEntityDao partyEntityDao;
    private Gson gson = new Gson();

    public static final int LIMIT_PER_LOAD = 10;

    public PartyStore(RestService restService, Repository repository) {
        this.restService = restService;
        this.partyEntityDao = repository.getPartyEntityDao();
    }

    private List<String> getList(String data) {
        if (data != null) {
            Gson gson = new Gson();
            return gson.fromJson(data, new TypeToken<List<String>>(){}.getType());
        }
        return null;
    }

    private List<Party> map(List<PartyEntity> partyList) {
        List<Party> result = new ArrayList<Party>();
        for (PartyEntity partyEntity: partyList) {
            Party party = map(partyEntity);
            result.add(party);
        }
        return result;
    }

    private Party map(PartyEntity partyEntity) {
        return new Party(
                partyEntity.getObjectId(),
                DateUtils.dateToString(partyEntity.getCreatedAt(), Constants.DateTime.FORMAT),
                partyEntity.getUserId(),
                partyEntity.getTitle(),
                gson.fromJson(partyEntity.getDate(), com.aumum.app.mobile.core.model.Date.class),
                gson.fromJson(partyEntity.getTime(), Time.class),
                partyEntity.getAddress(),
                partyEntity.getLatitude(),
                partyEntity.getLongitude(),
                partyEntity.getLocation(),
                partyEntity.getDetails(),
                partyEntity.getGroupId(),
                getList(partyEntity.getMembers()),
                getList(partyEntity.getLikes()),
                getList(partyEntity.getComments()),
                getList(partyEntity.getReasons()),
                getList(partyEntity.getFavorites()),
                getList(partyEntity.getImages()));
    }

    private PartyEntity map(Party party) throws Exception {
        Date createdAt = DateUtils.stringToDate(party.getCreatedAt(), Constants.DateTime.FORMAT);
        PartyEntity partyEntity = new PartyEntity(
                party.getObjectId(),
                createdAt,
                party.getUserId(),
                party.getTitle(),
                gson.toJson(party.getDate()),
                gson.toJson(party.getTime()),
                party.getAddress(),
                party.getLatitude(),
                party.getLongitude(),
                party.getLocation(),
                party.getDetails(),
                party.getGroupId(),
                gson.toJson(party.getMembers()),
                gson.toJson(party.getLikes()),
                gson.toJson(party.getComments()),
                gson.toJson(party.getReasons()),
                gson.toJson(party.getFavorites()),
                gson.toJson(party.getImages()));
        return partyEntity;
    }

    public void updateOrInsert(List<Party> partyList) throws Exception {
        for (Party party: partyList) {
            partyEntityDao.insertOrReplace(map(party));
        }
    }

    public List<Party> getUpwardsList(String userId, String time) throws Exception {
        QueryBuilder<PartyEntity> query = partyEntityDao.queryBuilder();
        if (time != null) {
            Date createdAt = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
            query = query.where(PartyEntityDao.Properties.CreatedAt.gt(createdAt));
        }
        List<PartyEntity> records = query
                .orderDesc(PartyEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        if (records.size() > 0) {
            return map(records);
        } else {
            int limit = time != null ? Integer.MAX_VALUE : LIMIT_PER_LOAD;
            List<Party> partyList = restService.getPartiesAfter(userId, time, limit);
            updateOrInsert(partyList);
            return partyList;
        }
    }

    public List<Party> getBackwardsList(String userId, String time) throws Exception {
        Date date = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
        List<PartyEntity> records = partyEntityDao.queryBuilder()
                .where(PartyEntityDao.Properties.CreatedAt.lt(date))
                .orderDesc(PartyEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        if (records.size() > 0) {
            return map(records);
        } else {
            List<Party> partyList = restService.getPartiesBefore(userId, time, LIMIT_PER_LOAD);
            updateOrInsert(partyList);
            return partyList;
        }
    }

    public List<Party> getBackwardsList(List<String> idList, String time) throws Exception {
        Date date = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
        List<PartyEntity> records = partyEntityDao.queryBuilder()
                .where(PartyEntityDao.Properties.ObjectId.in(idList))
                .where(PartyEntityDao.Properties.CreatedAt.lt(date))
                .orderDesc(PartyEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        if (records.size() > 0) {
            return map(records);
        } else {
            List<Party> partyList = restService.getPartiesBefore(idList, time, LIMIT_PER_LOAD);
            updateOrInsert(partyList);
            return partyList;
        }
    }

    public Party getPartyByIdFromServer(String id) throws Exception {
        Party party = restService.getPartyById(id);
        if (party.getDeletedAt() == null) {
            partyEntityDao.insertOrReplace(map(party));
        }
        return party;
    }

    public Party getPartyById(String id) throws Exception {
        PartyEntity partyEntity = partyEntityDao.load(id);
        if (partyEntity != null) {
            return map(partyEntity);
        }
        return null;
    }

    public int getUnreadCount(String userId) throws Exception {
        String time = getLastUpdateTime();
        return restService.getPartiesCountAfter(userId, time);
    }

    private String getLastUpdateTime() {
        PartyEntity partyEntity = partyEntityDao.queryBuilder()
                .orderDesc(PartyEntityDao.Properties.CreatedAt)
                .limit(1)
                .unique();
        if (partyEntity != null) {
            return DateUtils.dateToString(partyEntity.getCreatedAt(), Constants.DateTime.FORMAT);
        }
        return null;
    }

    public List<Party> getList(String userId,
                               List<String> idList,
                               int limit) throws Exception {
        List<Party> partyList = restService.getParties(userId, idList, limit);
        for (Party party: partyList) {
            partyEntityDao.insertOrReplace(map(party));
        }
        return partyList;
    }

    public List<Party> getRecentList(String userId, List<String> idList) throws Exception {
        return getList(userId, idList, LIMIT_PER_LOAD);
    }

    public List<Party> getAllList(String userId, List<String> idList) throws Exception {
        return getList(userId, idList, Integer.MAX_VALUE);
    }

    public void deleteParty(String partyId) {
        partyEntityDao.deleteByKey(partyId);
    }

    public List<Party> getNearByList(String userId,
                                     PlaceRange range,
                                     String time) throws Exception {
        List<Party> partyList = restService
                .getNearByPartiesBefore(userId, range, time, LIMIT_PER_LOAD);
        updateOrInsert(partyList);
        return partyList;
    }

    public void save(Party party) throws Exception {
        partyEntityDao.insertOrReplace(map(party));
    }
}
