package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.SpecialProduct;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Administrator on 10/03/2015.
 */
public interface SpecialProductService {

    @GET(Constants.Http.URL_SPECIAL_PRODUCTS_FRAG)
    ListWrapper<SpecialProduct> getList(@Query("where") String where,
                                        @Query("limit") int limit);
}
