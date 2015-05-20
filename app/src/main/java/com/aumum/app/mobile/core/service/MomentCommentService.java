package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.Comment;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Administrator on 14/05/2015.
 */
public interface MomentCommentService {

    @GET(Constants.Http.URL_MOMENT_COMMENTS_FRAG)
    ListWrapper<Comment> getList(@Query("order") String order,
                                 @Query("where") String where,
                                 @Query("limit") int limit);
}
