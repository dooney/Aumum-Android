package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.Area;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Administrator on 14/01/2015.
 */
public interface AreaService {

    @GET(Constants.Http.URL_AREAS_FRAG)
    ListWrapper<Area> getList(@Query("where") String where,
                              @Query("limit") int limit);
}
