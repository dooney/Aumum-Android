package com.aumum.app.mobile.core;

import com.google.gson.JsonObject;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Administrator on 26/09/2014.
 */
public interface PartyService {

    @GET(Constants.Http.URL_PARTIES_FRAG)
    ListWrapper<Party> getAll(@Query("order") String order,
                              @Query("where") String where,
                              @Query("limit") int limit);

    @POST(Constants.Http.URL_PARTIES_FRAG)
    Party newParty(@Body Party data);

    @PUT(Constants.Http.URL_PARTY_BY_ID_FRAG)
    JsonObject updateById(@Path("id") String id, @Body JsonObject data);
}
