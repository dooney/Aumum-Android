
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

    private MessageService getMessageService() {
        return getRestAdapter().create(MessageService.class);
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
            final JsonObject timeJson = new JsonObject();
            timeJson.addProperty("__type", "Date");
            timeJson.addProperty("iso", after.toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
            final JsonObject gtJson = new JsonObject();
            gtJson.add("$gt", timeJson);
            final JsonObject whereJson = new JsonObject();
            whereJson.add("createdAt", gtJson);
            where = whereJson.toString();
        }
        return getPartyService().getAll("-createdAt", where, limit).getResults();
    }

    public List<Party> getPartiesBefore(DateTime before, int limit) {
        final JsonObject timeJson = new JsonObject();
        timeJson.addProperty("__type", "Date");
        timeJson.addProperty("iso", before.toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        final JsonObject gtJson = new JsonObject();
        gtJson.add("$lt", timeJson);
        final JsonObject whereJson = new JsonObject();
        whereJson.add("createdAt", gtJson);
        String where = whereJson.toString();
        return getPartyService().getAll("-createdAt", where, limit).getResults();
    }

    private JsonObject updateFollower(JsonObject op, String userId, String followerUserId) {
        final JsonObject data = new JsonObject();
        final JsonArray newFollowers = new JsonArray();
        newFollowers.add(new JsonPrimitive(followerUserId));
        op.add("objects", newFollowers);
        data.add(Constants.Http.User.PARAM_FOLLOWERS, op);
        return getUserService().updateUserById(userId, data);
    }

    public JsonObject addFollower(String userId, String followerUserId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateFollower(op, userId, followerUserId);
    }

    public JsonObject removeFollower(String userId, String followerUserId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateFollower(op, userId, followerUserId);
    }

    private JsonObject updateFollowing(JsonObject op, String userId, String followingUserId) {
        final JsonObject data = new JsonObject();
        final JsonArray newFollowings = new JsonArray();
        newFollowings.add(new JsonPrimitive(followingUserId));
        op.add("objects", newFollowings);
        data.add(Constants.Http.User.PARAM_FOLLOWINGS, op);
        return getUserService().updateUserById(userId, data);
    }

    public JsonObject addFollowing(String userId, String followingUserId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateFollowing(op, userId, followingUserId);
    }

    public JsonObject removeFollowing(String userId, String followingUserId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateFollowing(op, userId, followingUserId);
    }

    public User getUserById(String id) {
        return getUserService().getById(id);
    }

    public Message newMessage(Message message) {
        return getMessageService().newMessage(message);
    }

    public JsonObject addUserMessage(String userId, String messageId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateUserMessage(op, userId, messageId);
    }

    private JsonObject updateUserMessage(JsonObject op, String userId, String messageId) {
        final JsonObject data = new JsonObject();
        final JsonArray messages = new JsonArray();
        messages.add(new JsonPrimitive(messageId));
        op.add("objects", messages);
        data.add(Constants.Http.User.PARAM_MESSAGES, op);
        return getUserService().updateUserById(userId, data);
    }

    public List<Message> getMessagesBefore(List<String> idList, DateTime after, int limit) {
        final JsonObject whereJson = new JsonObject();
        final JsonArray idListJson = new JsonArray();
        for (String id: idList) {
            idListJson.add(new JsonPrimitive(id));
        }
        final JsonObject inJson = new JsonObject();
        inJson.add("$in", idListJson);
        whereJson.add("objectId", inJson);
        if (after != null) {
            final JsonObject timeJson = new JsonObject();
            timeJson.addProperty("__type", "Date");
            timeJson.addProperty("iso", after.toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
            final JsonObject gtJson = new JsonObject();
            gtJson.add("$lt", timeJson);
            whereJson.add("createdAt", gtJson);
        }
        String where = whereJson.toString();
        return getMessageService().getMessages("-createdAt", where, limit).getResults();
    }
}