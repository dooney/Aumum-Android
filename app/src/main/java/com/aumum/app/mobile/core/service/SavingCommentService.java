package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.SavingComment;
import com.google.gson.JsonObject;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Administrator on 12/03/2015.
 */
public interface SavingCommentService {

    @GET(Constants.Http.URL_SAVING_COMMENTS_FRAG)
    ListWrapper<SavingComment> getList(@Query("order") String order,
                                       @Query("where") String where);

    @POST(Constants.Http.URL_SAVING_COMMENTS_FRAG)
    SavingComment newSavingComment(@Body SavingComment data);

    @PUT(Constants.Http.URL_SAVING_COMMENT_BY_ID_FRAG)
    JsonObject updateById(@Path("id") String id, @Body JsonObject data);
}
