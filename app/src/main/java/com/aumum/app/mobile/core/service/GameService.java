package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.Game;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Administrator on 26/03/2015.
 */
public interface GameService {

    @GET(Constants.Http.URL_GAMES_FRAG)
    ListWrapper<Game> getList(@Query("order") String order,
                              @Query("where") String where,
                              @Query("limit") int limit);
}
