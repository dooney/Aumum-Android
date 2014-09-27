
package com.aumum.app.mobile.core;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Bootstrap API service
 */
public class BootstrapService {

    private RestAdapter restAdapter;

    /**
     * Create bootstrap service
     * Default CTOR
     */
    public BootstrapService() {
    }

    /**
     * Create bootstrap service
     *
     * @param restAdapter The RestAdapter that allows HTTP Communication.
     */
    public BootstrapService(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    private UserService getUserService() {
        return getRestAdapter().create(UserService.class);
    }

    private PartyService getPartyService() {
        return getRestAdapter().create(PartyService.class);
    }

    private RestAdapter getRestAdapter() {
        return restAdapter;
    }

    /**
     * Get all bootstrap Users that exist on Parse.com
     */
    public List<User> getUsers() {
        return getUserService().getUsers().getResults();
    }

    public User authenticate(String username, String password) {
        return getUserService().authenticate(username, password);
    }

    public User register(String username, String password, String email, int area) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.PARAM_USERNAME, username);
        data.addProperty(Constants.Http.PARAM_PASSWORD, password);
        data.addProperty(Constants.Http.PARAM_EMAIL, email);
        data.addProperty(Constants.Http.PARAM_AREA, area);
        return getUserService().register(data);
    }
    
    public void resetPassword(String email) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.PARAM_EMAIL, email);
        getUserService().resetPassword(data);
    }

    public Party newParty(Party party) {
        return getPartyService().newParty(party);
    }

    public User getCurrentUser() {
        return getUserService().getMe();
    }

    public List<Party> getParties() {
        return getPartyService().getAll().getResults();
    }
}