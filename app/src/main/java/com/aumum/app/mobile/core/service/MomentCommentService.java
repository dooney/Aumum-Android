package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.MomentComment;
import com.google.gson.JsonObject;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Administrator on 3/03/2015.
 */
public interface MomentCommentService {

    @GET(Constants.Http.URL_MOMENT_COMMENTS_FRAG)
    ListWrapper<MomentComment> getList(@Query("order") String order,
                                      @Query("where") String where);

    @POST(Constants.Http.URL_MOMENT_COMMENTS_FRAG)
    MomentComment newMomentComment(@Body MomentComment data);

    @PUT(Constants.Http.URL_MOMENT_COMMENT_BY_ID_FRAG)
    JsonObject updateById(@Path("id") String id, @Body JsonObject data);
}
