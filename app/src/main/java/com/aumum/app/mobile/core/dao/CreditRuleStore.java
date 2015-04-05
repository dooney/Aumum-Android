package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.dao.entity.CreditRuleEntity;
import com.aumum.app.mobile.core.dao.gen.CreditRuleEntityDao;
import com.aumum.app.mobile.core.model.CreditRule;
import com.aumum.app.mobile.core.service.RestService;

import java.util.List;

/**
 * Created by Administrator on 5/04/2015.
 */
public class CreditRuleStore {

    private RestService restService;
    private CreditRuleEntityDao creditRuleEntityDao;

    public CreditRuleStore(RestService restService, Repository repository) {
        this.restService = restService;
        this.creditRuleEntityDao = repository.getCreditRuleEntityDao();
    }

    private CreditRuleEntity map(CreditRule creditRule) {
        return new CreditRuleEntity(
                creditRule.getObjectId(),
                creditRule.getSeq(),
                creditRule.getCredit(),
                creditRule.getDescription());
    }

    private CreditRule map(CreditRuleEntity creditRuleEntity) {
        return new CreditRule(
                creditRuleEntity.getObjectId(),
                creditRuleEntity.getSeq(),
                creditRuleEntity.getCredit(),
                creditRuleEntity.getDescription());
    }

    private void updateOrInsert(List<CreditRule> creditRuleList) throws Exception {
        for (CreditRule creditRule: creditRuleList) {
            updateOrInsert(creditRule);
        }
    }

    private void updateOrInsert(CreditRule creditRule) {
        creditRuleEntityDao.insertOrReplace(map(creditRule));
    }

    public void getAll() throws Exception {
        List<CreditRule> creditRuleList = restService.getCreditRuleList();
        updateOrInsert(creditRuleList);
    }

    public CreditRule getCreditRuleBySeq(int seq) {
        CreditRuleEntity creditRuleEntity = creditRuleEntityDao.queryBuilder()
                .where(CreditRuleEntityDao.Properties.Seq.eq(seq))
                .unique();
        if (creditRuleEntity != null) {
            return map(creditRuleEntity);
        }
        return null;
    }
}
