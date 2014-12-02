package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.PartyEntity;
import com.aumum.app.mobile.core.dao.gen.PartyEntityDao;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.Place;
import com.aumum.app.mobile.core.model.Time;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 30/09/2014.
 */
public class PartyStore {
    private RestService restService;
    private PartyEntityDao partyEntityDao;
    private Gson gson = new Gson();
    private List<Party> unreadList = new ArrayList<Party>();

    private int LIMIT_PER_LOAD = 15;

    public PartyStore(RestService restService, Repository repository) {
        this.restService = restService;
        this.partyEntityDao = repository.getPartyEntityDao();
    }

    public List<Party> getUnreadList() {
        return unreadList;
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
                gson.fromJson(partyEntity.getDate(), com.aumum.app.mobile.core.model.Date.class),
                gson.fromJson(partyEntity.getTime(), Time.class),
                partyEntity.getAge(),
                partyEntity.getGender(),
                partyEntity.getTitle(),
                gson.fromJson(partyEntity.getPlace(), Place.class),
                partyEntity.getDetails(),
                getList(partyEntity.getMembers()),
                getList(partyEntity.getLikes()),
                getList(partyEntity.getComments()),
                getList(partyEntity.getReasons()),
                getList(partyEntity.getFavorites()));
    }

    private PartyEntity map(Party party) throws Exception {
        Date createdAt = DateUtils.stringToDate(party.getCreatedAt(), Constants.DateTime.FORMAT);
        PartyEntity partyEntity = new PartyEntity(
                party.getObjectId(),
                createdAt,
                party.getUserId(),
                gson.toJson(party.getDate()),
                gson.toJson(party.getTime()),
                party.getAge(),
                party.getGender(),
                party.getTitle(),
                gson.toJson(party.getPlace()),
                party.getDetails(),
                gson.toJson(party.getMembers()),
                gson.toJson(party.getLikes()),
                gson.toJson(party.getComments()),
                gson.toJson(party.getReasons()),
                gson.toJson(party.getFavorites()));
        return partyEntity;
    }

    private void updateOrInsert(List<Party> partyList) throws Exception {
        for (Party party: partyList) {
            updateOrInsert(party);
        }
    }

    private void updateOrInsert(Party party) throws Exception {
        partyEntityDao.insertOrReplace(map(party));
    }

    public List<Party> getUpwardsList(String time) throws Exception {
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
            List<Party> partyList = restService.getPartiesAfter(time, limit);
            updateOrInsert(partyList);
            return partyList;
        }
    }

    public List<Party> getUpwardsList(List<String> idList, String time) throws Exception {
        QueryBuilder<PartyEntity> query = partyEntityDao.queryBuilder()
                .where(PartyEntityDao.Properties.ObjectId.in(idList));
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
            List<Party> partyList = restService.getPartiesAfter(idList, time, limit);
            updateOrInsert(partyList);
            return partyList;
        }
    }

    public List<Party> getBackwardsList(String time) throws Exception {
        Date date = DateUtils.stringToDate(time, Constants.DateTime.FORMAT);
        List<PartyEntity> records = partyEntityDao.queryBuilder()
                .where(PartyEntityDao.Properties.CreatedAt.lt(date))
                .orderDesc(PartyEntityDao.Properties.CreatedAt)
                .limit(LIMIT_PER_LOAD)
                .list();
        if (records.size() > 0) {
            return map(records);
        } else {
            List<Party> partyList = restService.getPartiesBefore(time, LIMIT_PER_LOAD);
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

    public void refresh(List<Party> partyList) throws Exception {
        if (partyList != null && partyList.size() > 0) {
            List<String> partyIds = new ArrayList<String>();
            for (Party party : partyList) {
                partyIds.add(party.getObjectId());
            }
            List<Party> resultList = restService.refreshParties(partyIds);
            HashMap<String, Party> hashMap = new HashMap<String, Party>();
            for (Party party : resultList) {
                hashMap.put(party.getObjectId(), party);
            }
            for (Party party: partyList) {
                Party result = hashMap.get(party.getObjectId());
                if (result != null) {
                    party.getMembers().clear();
                    party.getMembers().addAll(result.getMembers());

                    party.getComments().clear();
                    party.getComments().addAll(result.getComments());

                    party.getLikes().clear();
                    party.getLikes().addAll(result.getLikes());

                    updateOrInsert(party);
                }
            }
        }
    }

    public Party getPartyByIdFromServer(String id) throws Exception {
        Party party = restService.getPartyById(id);
        updateOrInsert(party);
        return party;
    }

    public Party getPartyById(String id) throws Exception {
        PartyEntity partyEntity = partyEntityDao.load(id);
        if (partyEntity != null) {
            return map(partyEntity);
        }
        return null;
    }

    public List<Party> getLiveListFromServer() throws Exception {
        List<Party> partyList = restService.getLiveParties();
        updateOrInsert(partyList);
        return partyList;
    }

    public List<Party> getUnreadListFromServer() throws Exception {
        String time = getLastUpdateTime();
        List<Party> partyList = restService.getPartiesAfter(time, Integer.MAX_VALUE);
        for (Party party: partyList) {
            partyEntityDao.insertOrReplace(map(party));
        }
        return partyList;
    }

    private String getLastUpdateTime() {
        PartyEntity partyEntity = partyEntityDao.queryBuilder()
                .orderDesc(PartyEntityDao.Properties.CreatedAt)
                .limit(1)
                .unique();
        if (partyEntity != null) {
            return DateUtils.dateToString(partyEntity.getCreatedAt(), Constants.DateTime.FORMAT);
        } else {
            DateTime now = DateTime.now(DateTimeZone.UTC);
            return now.toString(Constants.DateTime.FORMAT);
        }
    }

    public List<Party> getList(List<String> idList) throws Exception {
        List<Party> partyList = restService.getParties(idList);
        for (Party party: partyList) {
            partyEntityDao.insertOrReplace(map(party));
        }
        return partyList;
    }
}
