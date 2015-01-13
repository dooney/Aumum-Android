package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Feedback;
import com.google.gson.JsonObject;

import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Administrator on 13/01/2015.
 */
public interface FeedbackService {

    @POST(Constants.Http.URL_FEEDBACK_FRAG)
    Feedback newFeedback(@Body JsonObject data);
}
