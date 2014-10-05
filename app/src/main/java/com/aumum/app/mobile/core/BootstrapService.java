
package com.aumum.app.mobile.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.joda.time.DateTime;

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
    
    public JsonObject resetPassword(String email) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.PARAM_EMAIL, email);
        return getUserService().resetPassword(data);
    }

    public Party newParty(Party party) {
        return getPartyService().newParty(party);
    }

    public User getCurrentUser() {
        return getUserService().getMe();
    }

    public List<Party> getPartiesAfter(DateTime after, int limit) {
        String where = null;
        if (after != null) {
            where = "{\"createdAt\":{\"$gt\":{ \"__type\": \"Date\", \"iso\": \"" +
                after.toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") + "\" }}}";
        }
        return getPartyService().getAll("-createdAt", where, limit).getResults();
    }

    public List<Party> getPartiesBefore(DateTime before, int limit) {
        String where = "{\"createdAt\":{\"$lt\":{ \"__type\": \"Date\", \"iso\": \"" +
                before.toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") + "\" }}}";
        return getPartyService().getAll("-createdAt", where, limit).getResults();
    }

    public JsonObject addFollower(String userId, String followerUserId) {
        final JsonObject data = new JsonObject();
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        final JsonArray newFollowers = new JsonArray();
        newFollowers.add(new JsonPrimitive(followerUserId));
        op.add("objects", newFollowers);
        data.add(Constants.Http.User.PARAM_FOLLOWERS, op);
        return getUserService().updateUserById(userId, data);
    }

    public JsonObject addFollowing(String userId, String followingUserId) {
        final JsonObject data = new JsonObject();
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        final JsonArray newFollowings = new JsonArray();
        newFollowings.add(new JsonPrimitive(followingUserId));
        op.add("objects", newFollowings);
        data.add(Constants.Http.User.PARAM_FOLLOWINGS, op);
        return getUserService().updateUserById(userId, data);
    }
}