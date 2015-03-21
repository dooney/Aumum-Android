package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.EventCategory;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Administrator on 21/03/2015.
 */
public interface EventCategoryService {

    @GET(Constants.Http.URL_EVENT_CATEGORIES_FRAG)
    ListWrapper<EventCategory> getList(@Query("order") String order,
                                       @Query("where") String where,
                                       @Query("limit") int limit);
}
