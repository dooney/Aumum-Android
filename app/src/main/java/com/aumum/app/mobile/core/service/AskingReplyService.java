package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.AskingReply;
import com.google.gson.JsonObject;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Administrator on 30/11/2014.
 */
public interface AskingReplyService {

    @GET(Constants.Http.URL_ASKING_REPLIES_FRAG)
    ListWrapper<AskingReply> getList(@Query("order") String order,
                                     @Query("where") String where);

    @POST(Constants.Http.URL_ASKING_REPLIES_FRAG)
    AskingReply newAskingReply(@Body JsonObject data);

    @PUT(Constants.Http.URL_ASKING_REPLY_BY_ID_FRAG)
    JsonObject updateById(@Path("id") String id, @Body JsonObject data);
}
