package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.model.MomentComment;
import com.aumum.app.mobile.core.service.RestService;

import java.util.List;

/**
 * Created by Administrator on 3/03/2015.
 */
public class MomentCommentStore {

    private RestService restService;

    public MomentCommentStore(RestService restService) {
        this.restService = restService;
    }

    public List<MomentComment> getMomentComments(List<String> idList) {
        return restService.getMomentComments(idList);
    }
}
