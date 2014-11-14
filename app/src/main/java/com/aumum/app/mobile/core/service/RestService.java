
package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.model.Comment;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.PartyReason;
import com.aumum.app.mobile.core.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Bootstrap API service
 */
public class RestService {

    private RestAdapter restAdapter;

    /**
     * Create bootstrap service
     * Default CTOR
     */
    public RestService() {
    }

    /**
     * Create bootstrap service
     *
     * @param restAdapter The RestAdapter that allows HTTP Communication.
     */
    public RestService(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    private UserService getUserService() {
        return getRestAdapter().create(UserService.class);
    }

    private PartyService getPartyService() {
        return getRestAdapter().create(PartyService.class);
    }

    private PartyCommentService getPartyCommentService() {
        return getRestAdapter().create(PartyCommentService.class);
    }

    private MessageService getMessageService() {
        return getRestAdapter().create(MessageService.class);
    }

    private PartyReasonService getPartyReasonService() {
        return getRestAdapter().create(PartyReasonService.class);
    }

    private MomentService getMomentService() {
        return getRestAdapter().create(MomentService.class);
    }

    private RestAdapter getRestAdapter() {
        return restAdapter;
    }

    public User authenticate(String username, String password) {
        return getUserService().authenticate(username, password);
    }

    private JsonObject buildIdListJson(List<String> idList) {
        final JsonArray idListJson = new JsonArray();
        for (String id: idList) {
            idListJson.add(new JsonPrimitive(id));
        }
        final JsonObject objectIdInJson = new JsonObject();
        objectIdInJson.add("$in", idListJson);
        return objectIdInJson;
    }

    private JsonObject buildDateTimeAfterJson(String dateTime) {
        final JsonObject timeJson = new JsonObject();
        timeJson.addProperty("__type", "Date");
        timeJson.addProperty("iso", dateTime);
        final JsonObject gtJson = new JsonObject();
        gtJson.add("$gt", timeJson);
        return gtJson;
    }

    private JsonObject buildDateTimeBeforeJson(String dateTime) {
        final JsonObject timeJson = new JsonObject();
        timeJson.addProperty("__type", "Date");
        timeJson.addProperty("iso", dateTime);
        final JsonObject ltJson = new JsonObject();
        ltJson.add("$lt", timeJson);
        return ltJson;
    }

    public User register(String email, String password, String screenName, int area, String avatarUrl) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.PARAM_USERNAME, email);
        data.addProperty(Constants.Http.PARAM_PASSWORD, password);
        data.addProperty(Constants.Http.PARAM_EMAIL, email);
        data.addProperty(Constants.Http.PARAM_SCREEN_NAME, screenName);
        data.addProperty(Constants.Http.PARAM_AREA, area);
        data.addProperty(Constants.Http.PARAM_AVATAR_URL, avatarUrl);
        return getUserService().register(data);
    }
    
    public JsonObject resetPassword(String email) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.PARAM_EMAIL, email);
        return getUserService().resetPassword(data);
    }

    public Party newParty(Party party) {
        Gson gson = new Gson();
        JsonObject data = gson.toJsonTree(party).getAsJsonObject();
        final JsonObject dtJson = new JsonObject();
        dtJson.addProperty("__type", "Date");
        dtJson.addProperty("iso", party.getDateTime());
        data.add("dateTime", dtJson);
        return getPartyService().newParty(data);
    }

    public Party getPartyById(String id) {
        return getPartyService().getById(id);
    }

    private List<Party> getPartiesAfterCore(JsonObject whereJson, String after, int limit) {
        if (after != null) {
            whereJson.add("createdAt", buildDateTimeAfterJson(after));
        }
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getPartyService().getAll("-createdAt", where, limit).getResults();
    }

    public List<Party> getPartiesAfter(String after, int limit) {
        final JsonObject whereJson = new JsonObject();
        return getPartiesAfterCore(whereJson, after, limit);
    }

    public List<Party> getPartiesAfter(List<String> idList, String after, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        return getPartiesAfterCore(whereJson, after, limit);
    }

    private List<Party> getPartiesBeforeCore(JsonObject whereJson, String before, int limit) {
        whereJson.add("createdAt", buildDateTimeBeforeJson(before));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getPartyService().getAll("-createdAt", where, limit).getResults();
    }

    public List<Party> getPartiesBefore(String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        return getPartiesBeforeCore(whereJson, before, limit);
    }

    public List<Party> getPartiesBefore(List<String> idList, String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        return getPartiesBeforeCore(whereJson, before, limit);
    }

    private JsonObject updateFollower(JsonObject op, String userId, String followerUserId) {
        final JsonObject data = new JsonObject();
        final JsonArray followers = new JsonArray();
        followers.add(new JsonPrimitive(followerUserId));
        op.add("objects", followers);
        data.add(Constants.Http.User.PARAM_FOLLOWERS, op);
        return getUserService().updateById(userId, data);
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
        final JsonArray followings = new JsonArray();
        followings.add(new JsonPrimitive(followingUserId));
        op.add("objects", followings);
        data.add(Constants.Http.User.PARAM_FOLLOWINGS, op);
        return getUserService().updateById(userId, data);
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

    public JsonObject removeUserMessage(String userId, String messageId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateUserMessage(op, userId, messageId);
    }

    private JsonObject updateUserMessage(JsonObject op, String userId, String messageId) {
        final JsonObject data = new JsonObject();
        final JsonArray messages = new JsonArray();
        messages.add(new JsonPrimitive(messageId));
        op.add("objects", messages);
        data.add(Constants.Http.User.PARAM_MESSAGES, op);
        return getUserService().updateById(userId, data);
    }

    public List<Message> getMessagesAfter(List<String> idList, int[] typeList, String after, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        final JsonArray typeListJson = new JsonArray();
        for (int type: typeList) {
            typeListJson.add(new JsonPrimitive(type));
        }
        final JsonObject typeInJson = new JsonObject();
        typeInJson.add("$in", typeListJson);
        whereJson.add("type", typeInJson);
        if (after != null) {
            whereJson.add("createdAt", buildDateTimeAfterJson(after));
        }
        String where = whereJson.toString();
        return getMessageService().getMessages("-createdAt", where, limit).getResults();
    }

    public List<Message> getMessagesBefore(List<String> idList, int[] typeList, String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        final JsonArray typeListJson = new JsonArray();
        for (int type: typeList) {
            typeListJson.add(new JsonPrimitive(type));
        }
        final JsonObject typeInJson = new JsonObject();
        typeInJson.add("$in", typeListJson);
        whereJson.add("type", typeInJson);
        whereJson.add("createdAt", buildDateTimeBeforeJson(before));
        String where = whereJson.toString();
        return getMessageService().getMessages("-createdAt", where, limit).getResults();
    }

    public JsonObject addPartyMember(String partyId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updatePartyMembers(op, partyId, userId);
    }

    public JsonObject removePartyMember(String partyId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updatePartyMembers(op, partyId, userId);
    }

    private JsonObject updatePartyMembers(JsonObject op, String partyId, String userId) {
        final JsonObject data = new JsonObject();
        final JsonArray partyMembers = new JsonArray();
        partyMembers.add(new JsonPrimitive(userId));
        op.add("objects", partyMembers);
        data.add(Constants.Http.Party.PARAM_MEMBERS, op);
        return getPartyService().updateById(partyId, data);
    }

    public JsonObject addUserParty(String userId, String partyId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateUserParties(op, userId, partyId);
    }

    public JsonObject removeUserParty(String userId, String partyId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateUserParties(op, userId, partyId);
    }

    private JsonObject updateUserParties(JsonObject op, String userId, String partyId) {
        final JsonObject data = new JsonObject();
        final JsonArray userParties = new JsonArray();
        userParties.add(new JsonPrimitive(partyId));
        op.add("objects", userParties);
        data.add(Constants.Http.User.PARAM_PARTIES, op);
        return getUserService().updateById(userId, data);
    }

    public JsonObject addPartyFan(String partyId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updatePartyFans(op, partyId, userId);
    }

    public JsonObject removePartyFan(String partyId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updatePartyFans(op, partyId, userId);
    }

    private JsonObject updatePartyFans(JsonObject op, String partyId, String userId) {
        final JsonObject data = new JsonObject();
        final JsonArray partyFans = new JsonArray();
        partyFans.add(new JsonPrimitive(userId));
        op.add("objects", partyFans);
        data.add(Constants.Http.Party.PARAM_FANS, op);
        return getPartyService().updateById(partyId, data);
    }

    public List<Party> refreshParties(List<String> idList) {
        String keys = Constants.Http.Party.PARAM_MEMBERS + "," +
                Constants.Http.Party.PARAM_COMMENTS + "," +
                Constants.Http.Party.PARAM_FANS;
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        String where = whereJson.toString();
        return getPartyService().refresh(keys, where).getResults();
    }

    public List<Comment> getPartyComments(String partyId) {
        String keys = Constants.Http.Party.PARAM_COMMENTS;
        Party party = getPartyService().getFieldsById(partyId, keys);
        if (party != null) {
            final JsonObject whereJson = new JsonObject();
            whereJson.add("objectId", buildIdListJson(party.getComments()));
            String where = whereJson.toString();
            return getPartyCommentService().getPartyComments("-createdAt", where).getResults();
        }
        return null;
    }

    public Comment newPartyComment(Comment comment) {
        return getPartyCommentService().newPartyComment(comment);
    }

    public JsonObject addPartyComment(String partyId, String commentId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updatePartyComments(op, partyId, commentId);
    }

    public JsonObject removePartyComment(String partyId, String commentId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updatePartyComments(op, partyId, commentId);
    }

    private JsonObject updatePartyComments(JsonObject op, String partyId, String commentId) {
        final JsonObject data = new JsonObject();
        final JsonArray partyComments = new JsonArray();
        partyComments.add(new JsonPrimitive(commentId));
        op.add("objects", partyComments);
        data.add(Constants.Http.Party.PARAM_COMMENTS, op);
        return getPartyService().updateById(partyId, data);
    }

    public JsonObject addUserPartyPost(String userId, String partyId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateUserPartyPosts(op, userId, partyId);
    }

    public JsonObject removeUserPartyPost(String userId, String partyId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateUserPartyPosts(op, userId, partyId);
    }

    private JsonObject updateUserPartyPosts(JsonObject op, String userId, String partyId) {
        final JsonObject data = new JsonObject();
        final JsonArray userPartyPosts = new JsonArray();
        userPartyPosts.add(new JsonPrimitive(partyId));
        op.add("objects", userPartyPosts);
        data.add(Constants.Http.User.PARAM_PARTY_POSTS, op);
        return getUserService().updateById(userId, data);
    }

    public JsonObject updateUserAvatar(String userId, String avatarUrl) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.User.PARAM_AVATAR_URL, avatarUrl);
        return getUserService().updateById(userId, data);
    }

    public JsonObject deleteParty(String partyId) {
        final JsonObject data = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        data.addProperty("deletedAt", now.toString(Constants.DateTime.FORMAT));
        return getPartyService().updateById(partyId, data);
    }

    public JsonObject addUserComment(String userId, String commentId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateUserComments(op, userId, commentId);
    }

    public JsonObject removeUserComment(String userId, String commentId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateUserComments(op, userId, commentId);
    }

    private JsonObject updateUserComments(JsonObject op, String userId, String commentId) {
        final JsonObject data = new JsonObject();
        final JsonArray userComments = new JsonArray();
        userComments.add(new JsonPrimitive(commentId));
        op.add("objects", userComments);
        data.add(Constants.Http.User.PARAM_COMMENTS, op);
        return getUserService().updateById(userId, data);
    }

    public JsonObject deletePartyComment(String commentId) {
        final JsonObject data = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        data.addProperty("deletedAt", now.toString(Constants.DateTime.FORMAT));
        return getPartyCommentService().updateById(commentId, data);
    }

    public JsonObject deleteMessage(String messageId) {
        final JsonObject data = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        data.addProperty("deletedAt", now.toString(Constants.DateTime.FORMAT));
        return getMessageService().updateById(messageId, data);
    }

    public PartyReason newPartyReason(PartyReason reason) {
        return getPartyReasonService().newPartyReason(reason);
    }

    public JsonObject addPartyReasons(String partyId, String reasonId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updatePartyReasons(op, partyId, reasonId);
    }

    private JsonObject updatePartyReasons(JsonObject op, String partyId, String reasonId) {
        final JsonObject data = new JsonObject();
        final JsonArray partyReasons = new JsonArray();
        partyReasons.add(new JsonPrimitive(reasonId));
        op.add("objects", partyReasons);
        data.add(Constants.Http.Party.PARAM_REASONS, op);
        return getPartyService().updateById(partyId, data);
    }

    public List<PartyReason> getPartyReasons(String partyId) {
        String keys = Constants.Http.Party.PARAM_REASONS;
        Party party = getPartyService().getFieldsById(partyId, keys);
        if (party != null) {
            final JsonObject whereJson = new JsonObject();
            whereJson.add("objectId", buildIdListJson(party.getReasons()));
            String where = whereJson.toString();
            return getPartyReasonService().getPartyReasons("-createdAt", where).getResults();
        }
        return null;
    }

    public List<Party> getLiveParties() {
        final JsonObject whereJson = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        whereJson.add("dateTime", buildDateTimeAfterJson(now.toString(Constants.DateTime.FORMAT)));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getPartyService().getAll("dateTime", where, Integer.MAX_VALUE).getResults();
    }

    public List<Moment> getMomentsAfter(List<String> idList, String after, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        if (after != null) {
            whereJson.add("createdAt", buildDateTimeAfterJson(after));
        }
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getMomentService().getAll("-createdAt", where, limit).getResults();
    }

    public List<Moment> getMomentsBefore(List<String> idList, String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        whereJson.add("createdAt", buildDateTimeBeforeJson(before));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getMomentService().getAll("-createdAt", where, limit).getResults();
    }

    public Moment newMoment(Moment moment) {
        return getMomentService().newMoment(moment);
    }

    public JsonObject addUserMomentPost(String userId, String momentId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateUserMomentPosts(op, userId, momentId);
    }

    public JsonObject removeUserMomentPost(String userId, String momentId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateUserMomentPosts(op, userId, momentId);
    }

    private JsonObject updateUserMomentPosts(JsonObject op, String userId, String momentId) {
        final JsonObject data = new JsonObject();
        final JsonArray userMomentPosts = new JsonArray();
        userMomentPosts.add(new JsonPrimitive(momentId));
        op.add("objects", userMomentPosts);
        data.add(Constants.Http.User.PARAM_MOMENT_POSTS, op);
        return getUserService().updateById(userId, data);
    }

    public JsonObject addUserMoment(String userId, String momentId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateUserMoment(op, userId, momentId);
    }

    public JsonObject removeUserMoment(String userId, String momentId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateUserMoment(op, userId, momentId);
    }

    private JsonObject updateUserMoment(JsonObject op, String userId, String momentId) {
        final JsonObject data = new JsonObject();
        final JsonArray userMoments = new JsonArray();
        userMoments.add(new JsonPrimitive(momentId));
        op.add("objects", userMoments);
        data.add(Constants.Http.User.PARAM_MOMENTS, op);
        return getUserService().updateById(userId, data);
    }

    public JsonObject addPartyMoment(String partyId, String momentId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updatePartyMoments(op, partyId, momentId);
    }

    public JsonObject removePartyMoment(String partyId, String momentId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updatePartyMoments(op, partyId, momentId);
    }

    private JsonObject updatePartyMoments(JsonObject op, String partyId, String momentId) {
        final JsonObject data = new JsonObject();
        final JsonArray partyMoments = new JsonArray();
        partyMoments.add(new JsonPrimitive(momentId));
        op.add("objects", partyMoments);
        data.add(Constants.Http.Party.PARAM_MOMENTS, op);
        return getPartyService().updateById(partyId, data);
    }

    public int getMessagesCountAfter(String toUserId, int[] typeList, String time) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty("toUserId", toUserId);
        final JsonArray typeListJson = new JsonArray();
        for (int type: typeList) {
            typeListJson.add(new JsonPrimitive(type));
        }
        final JsonObject typeInJson = new JsonObject();
        typeInJson.add("$in", typeListJson);
        whereJson.add("type", typeInJson);
        whereJson.add("createdAt", buildDateTimeAfterJson(time));
        String where = whereJson.toString();
        JsonObject result = getMessageService().getMessagesCount("-createdAt", where, 1, 0);
        return result.get("count").getAsInt();
    }
}