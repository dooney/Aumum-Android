package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.model.Comment;
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

    @GET(Constants.Http.URL_COMMENTS_FRAG)
    ListWrapper<Comment> getPartyComments(@Query("order") String order,
                                          @Query("where") String where);

    @POST(Constants.Http.URL_COMMENTS_FRAG)
    Comment newPartyComment(@Body Comment data);

    @PUT(Constants.Http.URL_PARTY_COMMENT_BY_ID_FRAG)
    JsonObject updateById(@Path("id") String id, @Body JsonObject data);
}
