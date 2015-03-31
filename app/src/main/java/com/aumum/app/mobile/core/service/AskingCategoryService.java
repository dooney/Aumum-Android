package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.AskingCategory;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Administrator on 31/03/2015.
 */
public interface AskingCategoryService {

    @GET(Constants.Http.URL_ASKING_CATEGORIES_FRAG)
    ListWrapper<AskingCategory> getList(@Query("order") String order,
                                        @Query("where") String where,
                                        @Query("limit") int limit);
}
