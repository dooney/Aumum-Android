package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.PartyRequest;
import com.google.gson.JsonObject;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Administrator on 13/03/2015.
 */
public interface PartyRequestService {

    @GET(Constants.Http.URL_PARTY_REQUESTS_FRAG)
    ListWrapper<PartyRequest> getList(@Query("order") String order,
                                      @Query("where") String where,
                                      @Query("limit") int limit);

    @POST(Constants.Http.URL_PARTY_REQUESTS_FRAG)
    PartyRequest newPartyRequest(@Body JsonObject data);

    @PUT(Constants.Http.URL_PARTY_REQUEST_BY_ID_FRAG)
    JsonObject updateById(@Path("id") String id, @Body JsonObject data);

    @GET(Constants.Http.URL_PARTY_REQUESTS_FRAG)
    JsonObject getCount(@Query("where") String where,
                        @Query("count") int count,
                        @Query("limit") int limit);
}
