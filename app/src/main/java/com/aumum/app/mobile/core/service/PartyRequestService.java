package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.PartyRequest;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Administrator on 13/03/2015.
 */
public interface PartyRequestService {

    @GET(Constants.Http.URL_PARTY_REQUESTS_FRAG)
    ListWrapper<PartyRequest> getList(@Query("order") String order,
                                      @Query("where") String where,
                                      @Query("limit") int limit);
}
