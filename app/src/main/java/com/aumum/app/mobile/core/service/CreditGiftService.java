package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.CreditGift;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Administrator on 28/03/2015.
 */
public interface CreditGiftService {

    @GET(Constants.Http.URL_CREDIT_GIFTS_FRAG)
    ListWrapper<CreditGift> getList(@Query("order") String order,
                                    @Query("where") String where,
                                    @Query("limit") int limit);
}
