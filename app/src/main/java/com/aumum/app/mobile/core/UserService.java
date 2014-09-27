package com.aumum.app.mobile.core;

import com.google.gson.JsonObject;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * User service for connecting the the REST API and
 * getting the users.
 */
public interface UserService {

    @GET(Constants.Http.URL_USERS_FRAG)
    UsersWrapper getUsers();

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

    @GET(Constants.Http.URL_USERS_ME_FRAG)
    User getMe();
}
