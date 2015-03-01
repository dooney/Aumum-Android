package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.UserTag;

import retrofit.http.GET;

/**
 * Created by Administrator on 1/03/2015.
 */
public interface UserTagService {

    @GET(Constants.Http.URL_USER_TAGS_FRAG)
    ListWrapper<UserTag> getList();
}
