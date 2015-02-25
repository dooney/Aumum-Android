package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.CityGroup;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Administrator on 25/02/2015.
 */
public interface CityGroupService {

    @GET(Constants.Http.URL_CITY_GROUP_FRAG)
    ListWrapper<CityGroup> getList(@Query("where") String where,
                                   @Query("limit") int limit);
}
