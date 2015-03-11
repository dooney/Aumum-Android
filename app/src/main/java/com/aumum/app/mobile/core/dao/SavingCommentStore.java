package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.model.SavingComment;
import com.aumum.app.mobile.core.service.RestService;

import java.util.List;

/**
 * Created by Administrator on 12/03/2015.
 */
public class SavingCommentStore {

    private RestService restService;

    public SavingCommentStore(RestService restService) {
        this.restService = restService;
    }

    public List<SavingComment> getSavingComments(List<String> idList) {
        return restService.getSavingComments(idList);
    }
}
