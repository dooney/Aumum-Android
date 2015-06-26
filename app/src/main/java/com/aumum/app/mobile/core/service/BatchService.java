package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by Administrator on 6/01/2015.
 */
public interface BatchService {

    @POST(Constants.Http.URL_BATCH_FRAG)
    JsonArray execute(@Body JsonObject script);

    @Headers(Constants.Http.HEADER_PARSE_MASTER_KEY + ": " + Constants.Http.PARSE_MASTER_KEY)
    @POST(Constants.Http.URL_BATCH_FRAG)
    JsonArray executeWithKey(@Body JsonObject script);
}
