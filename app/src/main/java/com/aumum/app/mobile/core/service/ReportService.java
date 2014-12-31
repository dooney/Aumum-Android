package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Report;
import com.google.gson.JsonObject;

import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Administrator on 31/12/2014.
 */
public interface ReportService {

    @POST(Constants.Http.URL_REPORTS_FRAG)
    Report newReport(@Body JsonObject data);
}
