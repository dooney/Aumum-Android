package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.Asking;
import com.google.gson.JsonObject;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Administrator on 27/11/2014.
 */
public interface AskingService {

    @GET(Constants.Http.URL_ASKINGS_FRAG)
    ListWrapper<Asking> getAll(@Query("order") String order,
                               @Query("where") String where,
                               @Query("limit") int limit);

    @POST(Constants.Http.URL_ASKINGS_FRAG)
    Asking newAsking(@Body JsonObject data);

    @GET(Constants.Http.URL_ASKING_BY_ID_FRAG)
    Asking getById(@Path("id") String id);
}
