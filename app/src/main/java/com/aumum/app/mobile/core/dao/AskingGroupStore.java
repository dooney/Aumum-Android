package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.dao.entity.AskingGroupEntity;
import com.aumum.app.mobile.core.dao.gen.AskingGroupEntityDao;
import com.aumum.app.mobile.core.model.AskingGroup;
import com.aumum.app.mobile.core.service.RestService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 3/04/2015.
 */
public class AskingGroupStore {

    private RestService restService;
    private AskingGroupEntityDao askingGroupEntityDao;

    public AskingGroupStore(RestService restService, Repository repository) {
        this.restService = restService;
        this.askingGroupEntityDao = repository.getAskingGroupEntityDao();
    }

    private List<AskingGroup> map(List<AskingGroupEntity> askingGroupList) {
        List<AskingGroup> result = new ArrayList<AskingGroup>();
        for (AskingGroupEntity askingGroupEntity: askingGroupList) {
            AskingGroup asking = map(askingGroupEntity);
            result.add(asking);
        }
        return result;
    }

    private AskingGroup map(AskingGroupEntity askingGroupEntity) {
        return new AskingGroup(
                askingGroupEntity.getObjectId(),
                askingGroupEntity.getAvatarUrl(),
                askingGroupEntity.getScreenName(),
                askingGroupEntity.getDescription(),
                askingGroupEntity.getSeq(),
                askingGroupEntity.getBoardId());
    }

    private AskingGroupEntity map(AskingGroup askingGroup) throws Exception {
        return new AskingGroupEntity(
                askingGroup.getObjectId(),
                askingGroup.getAvatarUrl(),
                askingGroup.getScreenName(),
                askingGroup.getDescription(),
                askingGroup.getSeq(),
                askingGroup.getBoardId());
    }

    public void updateOrInsert(List<AskingGroup> askingGroupList) throws Exception {
        for (AskingGroup askingGroup: askingGroupList) {
            updateOrInsert(askingGroup);
        }
    }

    private void updateOrInsert(AskingGroup askingGroup) throws Exception {
        askingGroupEntityDao.insertOrReplace(map(askingGroup));
    }

    public List<AskingGroup> getList(List<String> idList) throws Exception {
        List<AskingGroupEntity> records = askingGroupEntityDao.queryBuilder()
                .where(AskingGroupEntityDao.Properties.ObjectId.in(idList))
                .orderAsc(AskingGroupEntityDao.Properties.Seq)
                .list();
        if (records.size() > 0) {
            return map(records);
        } else {
            List<AskingGroup> askingGroupList = restService.getAskingGroupList(idList);
            updateOrInsert(askingGroupList);
            return askingGroupList;
        }
    }

    public List<AskingGroup> getListByBoardId(String boardId) throws Exception {
        List<AskingGroupEntity> records = askingGroupEntityDao.queryBuilder()
                .where(AskingGroupEntityDao.Properties.BoardId.eq(boardId))
                .orderAsc(AskingGroupEntityDao.Properties.Seq)
                .list();
        if (records.size() > 0) {
            return map(records);
        } else {
            List<AskingGroup> askingGroupList = restService.getAskingGroupListByBoardId(boardId);
            updateOrInsert(askingGroupList);
            return askingGroupList;
        }
    }
}
