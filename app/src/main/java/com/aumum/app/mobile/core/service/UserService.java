package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.api.ListWrapper;
import com.aumum.app.mobile.core.model.User;
import com.google.gson.JsonObject;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * User service for connecting the the REST API and
 * getting the users.
 */
public interface UserService {
    /**
     * The {@link retrofit.http.Query} values will be transform into query string paramters
     * via Retrofit
     *
     * @param username The users name
     * @param password The users password
     * @return A login response.
     */
    @GET(Constants.Http.URL_LOGIN_FRAG)
    User authenticate(@Query(Constants.Http.PARAM_USERNAME) String username,
                      @Query(Constants.Http.PARAM_PASSWORD) String password);

    @POST(Constants.Http.URL_USERS_FRAG)
    User register(@Body JsonObject data);

    @POST(Constants.Http.URL_RESET_PASSWORD_FRAG)
    JsonObject resetPassword(@Body JsonObject data);

    @Headers(Constants.Http.HEADER_PARSE_MASTER_KEY + ": " + Constants.Http.PARSE_MASTER_KEY)
    @PUT(Constants.Http.URL_USER_BY_ID_FRAG)
    JsonObject updateById(@Path("id") String id, @Body JsonObject data);

    @GET(Constants.Http.URL_USER_BY_ID_FRAG)
    User getById(@Path("id") String id);

    @GET(Constants.Http.URL_USERS_FRAG)
    ListWrapper<User> getList(@Query("where") String where,
                              @Query("limit") int limit);

    @GET(Constants.Http.URL_USERS_FRAG)
    ListWrapper<User> getByScreenName(@Query("where") String where,
                                      @Query("limit") int limit);

    @GET(Constants.Http.URL_USERS_FRAG)
    ListWrapper<JsonObject> getInAppContactList(@Query("keys") String keys,
                                                @Query("where") String where);
}
