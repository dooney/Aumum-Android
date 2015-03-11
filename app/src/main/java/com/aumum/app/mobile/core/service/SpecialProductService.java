package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.SpecialProduct;
import com.google.gson.JsonObject;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Administrator on 10/03/2015.
 */
public interface SpecialProductService {

    @GET(Constants.Http.URL_SPECIAL_PRODUCTS_FRAG)
    ListWrapper<SpecialProduct> getList(@Query("order") String order,
                                        @Query("where") String where,
                                        @Query("limit") int limit);

    @PUT(Constants.Http.URL_SPECIAL_PRODUCT_BY_ID_FRAG)
    JsonObject updateById(@Path("id") String id, @Body JsonObject data);
}
