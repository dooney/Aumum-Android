package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.Party;
import com.google.gson.JsonObject;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Administrator on 26/09/2014.
 */
public interface PartyService {

    @GET(Constants.Http.URL_PARTIES_FRAG)
    ListWrapper<Party> getList(@Query("order") String order,
                               @Query("where") String where,
                               @Query("limit") int limit);

    @POST(Constants.Http.URL_PARTIES_FRAG)
    Party newParty(@Body JsonObject data);

    @PUT(Constants.Http.URL_PARTY_BY_ID_FRAG)
    JsonObject updateById(@Path("id") String id, @Body JsonObject data);

    @GET(Constants.Http.URL_PARTY_BY_ID_FRAG)
    Party getById(@Path("id") String id);

    @GET(Constants.Http.URL_PARTIES_FRAG)
    JsonObject getCount(@Query("where") String where,
                        @Query("count") int count,
                        @Query("limit") int limit);

    @GET(Constants.Http.URL_PARTY_BY_ID_FRAG)
    JsonObject getMembers(@Path("id") String id,
                          @Query("keys") String keys);
}
