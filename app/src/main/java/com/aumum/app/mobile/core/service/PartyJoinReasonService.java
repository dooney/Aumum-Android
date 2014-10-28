package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.PartyJoinReason;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Administrator on 28/10/2014.
 */
public interface PartyJoinReasonService {

    @GET(Constants.Http.URL_PARTY_JOIN_REASONS_FRAG)
    ListWrapper<PartyJoinReason> getPartyJoinReasons(@Query("order") String order,
                                                     @Query("where") String where);

    @POST(Constants.Http.URL_PARTY_JOIN_REASONS_FRAG)
    PartyJoinReason newPartyJoinReason(@Body PartyJoinReason data);
}
