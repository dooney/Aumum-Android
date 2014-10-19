package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.Message;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Administrator on 7/10/2014.
 */
public interface MessageService {

    @GET(Constants.Http.URL_MESSAGES_FRAG)
    ListWrapper<Message> getMessages(@Query("order") String order,
                                     @Query("where") String where,
                                     @Query("limit") int limit);

    @POST(Constants.Http.URL_MESSAGES_FRAG)
    Message newMessage(@Body Message data);
}
