package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.Moment;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Administrator on 8/11/2014.
 */
public interface MomentService {

    @GET(Constants.Http.URL_MOMENTS_FRAG)
    ListWrapper<Moment> getAll(@Query("order") String order,
                               @Query("where") String where,
                               @Query("limit") int limit);

    @POST(Constants.Http.URL_MOMENTS_FRAG)
    Moment newMoment(@Body Moment data);
}
