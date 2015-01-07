package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.model.PartyComment;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.google.gson.JsonObject;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Administrator on 11/10/2014.
 */
public interface PartyCommentService {

    @GET(Constants.Http.URL_PARTY_COMMENTS_FRAG)
    ListWrapper<PartyComment> getList(@Query("order") String order,
                                 @Query("where") String where);

    @POST(Constants.Http.URL_PARTY_COMMENTS_FRAG)
    PartyComment newPartyComment(@Body PartyComment data);

    @PUT(Constants.Http.URL_PARTY_COMMENT_BY_ID_FRAG)
    JsonObject updateById(@Path("id") String id, @Body JsonObject data);
}
