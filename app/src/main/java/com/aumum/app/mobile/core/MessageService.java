package com.aumum.app.mobile.core;

import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Administrator on 7/10/2014.
 */
public interface MessageService {

    @POST(Constants.Http.URL_MESSAGES_FRAG)
    Message newMessage(@Body Message data);
}
