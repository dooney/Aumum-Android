package com.aumum.app.mobile.core;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
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
}
