package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.PartyReason;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Administrator on 28/10/2014.
 */
public interface PartyReasonService {

    @GET(Constants.Http.URL_PARTY_REASONS_FRAG)
    ListWrapper<PartyReason> getList(@Query("order") String order,
                                     @Query("where") String where);

    @POST(Constants.Http.URL_PARTY_REASONS_FRAG)
    PartyReason newPartyReason(@Body PartyReason data);
}
