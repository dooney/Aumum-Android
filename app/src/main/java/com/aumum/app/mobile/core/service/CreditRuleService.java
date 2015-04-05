package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.CreditRule;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Administrator on 5/04/2015.
 */
public interface CreditRuleService {

    @GET(Constants.Http.URL_CREDIT_RULES_FRAG)
    ListWrapper<CreditRule> getList(@Query("order") String order,
                                    @Query("where") String where,
                                    @Query("limit") int limit);
}
