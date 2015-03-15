package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.Feed;

import retrofit.http.GET;

/**
 * Created by Administrator on 15/03/2015.
 */
public interface FeedService {

    @GET(Constants.Http.URL_FEEDS_FRAG)
    ListWrapper<Feed> getList();
}
