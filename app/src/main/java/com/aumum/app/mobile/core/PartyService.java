package com.aumum.app.mobile.core;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by Administrator on 26/09/2014.
 */
public interface PartyService {

    @GET(Constants.Http.URL_PARTIES_FRAG)
    ListWrapper<Party> getAll();

    @POST(Constants.Http.URL_PARTIES_FRAG)
    Party newParty(@Body Party data);
}
