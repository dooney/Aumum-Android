package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.CreditOrder;

import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Administrator on 28/03/2015.
 */
public interface CreditOrderService {

    @POST(Constants.Http.URL_CREDIT_ORDERS_FRAG)
    CreditOrder newCreditOrder(@Body CreditOrder data);
}
