package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.AskingBoard;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Administrator on 2/04/2015.
 */
public interface AskingBoardService {

    @GET(Constants.Http.URL_ASKING_BOARDS_FRAG)
    ListWrapper<AskingBoard> getList(@Query("order") String order,
                                     @Query("where") String where,
                                     @Query("limit") int limit);
}
