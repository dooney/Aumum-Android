package com.aumum.app.mobile.core;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
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
}
