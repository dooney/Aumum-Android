
package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.model.AskingReply;
import com.aumum.app.mobile.core.model.Feedback;
import com.aumum.app.mobile.core.model.PartyComment;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.PartyReason;
import com.aumum.app.mobile.core.model.PlaceRange;
import com.aumum.app.mobile.core.model.Report;
import com.aumum.app.mobile.core.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    private BatchService getBatchService() {
        return getRestAdapter().create(BatchService.class);
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

    private PartyReasonService getPartyReasonService() {
        return getRestAdapter().create(PartyReasonService.class);
    }

    private AskingService getAskingService() {
        return getRestAdapter().create(AskingService.class);
    }

    private AskingReplyService getAskingReplyService() {
        return getRestAdapter().create(AskingReplyService.class);
    }

    private ReportService getReportService() {
        return getRestAdapter().create(ReportService.class);
    }

    private FeedbackService getFeedbackService() {
        return getRestAdapter().create(FeedbackService.class);
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

    private JsonObject buildDateTimeBetweenJson(String start, String end) {
        final JsonObject startJson = new JsonObject();
        startJson.addProperty("__type", "Date");
        startJson.addProperty("iso", start);
        final JsonObject endJson = new JsonObject();
        endJson.addProperty("__type", "Date");
        endJson.addProperty("iso", end);
        final JsonObject timeJson = new JsonObject();
        timeJson.add("$gt", startJson);
        timeJson.add("$lt", endJson);
        return timeJson;
    }

    private JsonObject buildRequestJson(String path, JsonObject body) {
        final JsonObject requestJson = new JsonObject();
        requestJson.addProperty("method", "PUT");
        requestJson.addProperty("path", path);
        requestJson.add("body", body);
        return requestJson;
    }

    public boolean getMobileRegistered(String mobile) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty(Constants.Http.PARAM_USERNAME, mobile);
        String where = whereJson.toString();
        return getUserService().getList(where, 1).getResults().size() > 0;
    }

    public boolean getScreenNameRegistered(String screenName) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty(Constants.Http.User.PARAM_SCREEN_NAME, screenName);
        String where = whereJson.toString();
        return getUserService().getList(where, 1).getResults().size() > 0;
    }

    public boolean getEmailRegistered(String email) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty(Constants.Http.User.PARAM_EMAIL, email);
        String where = whereJson.toString();
        return getUserService().getList(where, 1).getResults().size() > 0;
    }

    public User register(String mobile, String password) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.PARAM_USERNAME, mobile);
        data.addProperty(Constants.Http.PARAM_PASSWORD, password);
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
        whereJson.add("deletedAt", liveJson);
        String where = whereJson.toString();
        return getPartyService().getList("-createdAt", where, limit).getResults();
    }

    private JsonElement buildSubscriptionJson(String userId, boolean includeOwner) {
        JsonArray jsonArray = new JsonArray();
        if (includeOwner) {
            JsonObject ownerJson = new JsonObject();
            ownerJson.addProperty("userId", userId);
            jsonArray.add(ownerJson);
        }
        JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        JsonObject publicJson = new JsonObject();
        publicJson.add("subscriptions", liveJson);
        jsonArray.add(publicJson);
        JsonObject inJson = new JsonObject();
        inJson.addProperty("subscriptions", userId);
        jsonArray.add(inJson);
        return jsonArray;
    }

    public List<Party> getPartiesAfter(String userId, String after, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("$or", buildSubscriptionJson(userId, true));
        return getPartiesAfterCore(whereJson, after, limit);
    }

    public int getPartiesCountAfter(String userId, String after) {
        final JsonObject whereJson = new JsonObject();
        JsonObject notOwnerJson = new JsonObject();
        notOwnerJson.addProperty("$ne", userId);
        whereJson.add("userId", notOwnerJson);
        whereJson.add("$or", buildSubscriptionJson(userId, false));
        if (after != null) {
            whereJson.add("createdAt", buildDateTimeAfterJson(after));
        }
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        JsonObject result = getPartyService().getCount(where, 1, 0);
        return result.get("count").getAsInt();
    }

    private List<Party> getPartiesBeforeCore(JsonObject whereJson, String before, int limit) {
        whereJson.add("createdAt", buildDateTimeBeforeJson(before));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getPartyService().getList("-createdAt", where, limit).getResults();
    }

    public List<Party> getPartiesBefore(String userId, String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("$or", buildSubscriptionJson(userId, true));
        return getPartiesBeforeCore(whereJson, before, limit);
    }

    public List<Party> getPartiesBefore(List<String> idList, String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        return getPartiesBeforeCore(whereJson, before, limit);
    }

    public List<Party> getParties(String userId, List<String> idList, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("$or", buildSubscriptionJson(userId, true));
        whereJson.add("objectId", buildIdListJson(idList));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt", liveJson);
        String where = whereJson.toString();
        return getPartyService().getList("-createdAt", where, limit).getResults();
    }

    public List<Party> getParties(String userId, List<String> idList, DateTime start, DateTime end) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("$or", buildSubscriptionJson(userId, true));
        whereJson.add("objectId", buildIdListJson(idList));
        whereJson.add("dateTime", buildDateTimeBetweenJson(
                start.toString(Constants.DateTime.FORMAT),
                end.toString(Constants.DateTime.FORMAT)));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt", liveJson);
        String where = whereJson.toString();
        return getPartyService().getList("dateTime", where, Integer.MAX_VALUE).getResults();
    }

    public List<Party> getNearByPartiesBefore(String userId, PlaceRange range, String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("$or", buildSubscriptionJson(userId, true));
        if (before != null) {
            whereJson.add("createdAt", buildDateTimeBeforeJson(before));
        }
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);

        JsonObject latJson = new JsonObject();
        latJson.addProperty("$gt", range.getMinLat());
        latJson.addProperty("$lt", range.getMaxLat());
        whereJson.add("latitude", latJson);

        JsonObject lngJson = new JsonObject();
        lngJson.addProperty("$gt", range.getMinLng());
        lngJson.addProperty("$lt", range.getMaxLng());
        whereJson.add("longitude", lngJson);

        String where = whereJson.toString();
        return getPartyService().getList("-createdAt", where, limit).getResults();
    }

    private JsonObject updateContact(JsonObject op, String userId, String contactId) {
        final JsonObject data = new JsonObject();
        final JsonArray contacts = new JsonArray();
        contacts.add(new JsonPrimitive(contactId));
        op.add("objects", contacts);
        data.add(Constants.Http.User.PARAM_CONTACTS, op);
        return getUserService().updateById(userId, data);
    }

    public JsonObject addContact(String userId, String contactId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateContact(op, userId, contactId);
    }

    public JsonObject removeContact(String userId, String contactId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateContact(op, userId, contactId);
    }

    public User getUserById(String id) {
        return getUserService().getById(id);
    }

    public User getUserByScreenName(String screenName) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty("screenName", screenName);
        String where = whereJson.toString();
        return getUserService().getByScreenName(where, 1).getResults().get(0);
    }

    public User getUserByChatId(String id) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty("chatId", id);
        String where = whereJson.toString();
        return getUserService().getList(where, 1).getResults().get(0);
    }

    public String getUserByName(String name) {
        final JsonObject whereJson = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        JsonObject screenNameJson = new JsonObject();
        screenNameJson.addProperty("screenName", name);
        jsonArray.add(screenNameJson);
        JsonObject userNameJson = new JsonObject();
        userNameJson.addProperty(Constants.Http.PARAM_USERNAME, name);
        jsonArray.add(userNameJson);
        whereJson.add("$or", jsonArray);
        String where = whereJson.toString();
        List<JsonObject> results = getUserService().getList("objectId", where).getResults();
        if (results.size() > 0) {
            return results.get(0).get("objectId").getAsString();
        }
        return null;
    }

    private JsonObject buildPartyMembersRequestJson(String op, String partyId, String userId) {
        final JsonObject body = new JsonObject();
        final JsonArray partyMembers = new JsonArray();
        partyMembers.add(new JsonPrimitive(userId));
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", op);
        opJson.add("objects", partyMembers);
        body.add(Constants.Http.Party.PARAM_MEMBERS, opJson);
        String path = Constants.Http.URL_PARTIES_FRAG + "/" + partyId;
        return buildRequestJson(path, body);
    }

    private JsonObject buildUserPartiesRequestJson(String op, String userId, String partyId) {
        final JsonObject body = new JsonObject();
        final JsonArray userParties = new JsonArray();
        userParties.add(new JsonPrimitive(partyId));
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", op);
        opJson.add("objects", userParties);
        body.add(Constants.Http.User.PARAM_PARTIES, opJson);
        String path = Constants.Http.URL_USERS_FRAG + "/" + userId;
        return buildRequestJson(path, body);
    }

    private JsonObject buildPartyReasonsRequestJson(String op, String partyId, String reasonId) {
        final JsonObject body = new JsonObject();
        final JsonArray partyReasons = new JsonArray();
        partyReasons.add(new JsonPrimitive(reasonId));
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", op);
        opJson.add("objects", partyReasons);
        body.add(Constants.Http.Party.PARAM_REASONS, opJson);
        String path = Constants.Http.URL_PARTIES_FRAG + "/" + partyId;
        return buildRequestJson(path, body);
    }

    private JsonArray updatePartyMembers(String op, String partyId, String userId, String reasonId) {
        final JsonObject script = new JsonObject();
        final JsonArray requests = new JsonArray();
        requests.add(buildPartyMembersRequestJson(op, partyId, userId));
        requests.add(buildUserPartiesRequestJson(op, userId, partyId));
        if (reasonId != null) {
            requests.add(buildPartyReasonsRequestJson(op, partyId, reasonId));
        }
        script.add(Constants.Http.Batch.PARAM_REQUESTS, requests);
        return getBatchService().execute(script);
    }

    public JsonArray joinParty(String partyId, String userId, String reasonId) {
        return updatePartyMembers("AddUnique", partyId, userId, reasonId);
    }

    public JsonArray quitParty(String partyId, String userId, String reasonId) {
        return updatePartyMembers("Remove", partyId, userId, reasonId);
    }

    public List<String> getPartyMembers(String partyId) {
        JsonObject json = getPartyService().getMembers(partyId,
                Constants.Http.Party.PARAM_MEMBERS);
        ArrayList<String> members = new ArrayList<String>();
        JsonArray array = json.get(Constants.Http.Party.PARAM_MEMBERS).getAsJsonArray();
        for (Iterator<JsonElement> it = array.iterator(); it.hasNext();) {
            members.add(it.next().getAsString());
        }
        return members;
    }

    public JsonObject addPartyLike(String partyId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updatePartyLikes(op, partyId, userId);
    }

    public JsonObject removePartyLike(String partyId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updatePartyLikes(op, partyId, userId);
    }

    private JsonObject updatePartyLikes(JsonObject op, String partyId, String userId) {
        final JsonObject data = new JsonObject();
        final JsonArray partyLikes = new JsonArray();
        partyLikes.add(new JsonPrimitive(userId));
        op.add("objects", partyLikes);
        data.add(Constants.Http.Party.PARAM_LIKES, op);
        return getPartyService().updateById(partyId, data);
    }

    public List<PartyComment> getPartyComments(List<String> idList) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getPartyCommentService().getList("-createdAt", where).getResults();
    }

    public PartyComment newPartyComment(PartyComment comment) {
        return getPartyCommentService().newPartyComment(comment);
    }

    public JsonObject addPartyComment(String partyId, String commentId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
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

    public JsonObject addPartyGroup(String partyId, String groupId) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.Party.PARAM_GROUP_ID, groupId);
        return getPartyService().updateById(partyId, data);
    }

    public JsonObject updateUserAvatar(String userId, String avatarUrl) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.User.PARAM_AVATAR_URL, avatarUrl);
        return getUserService().updateById(userId, data);
    }

    public JsonObject updateUserProfile(User user) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.User.PARAM_SCREEN_NAME, user.getScreenName());
        data.addProperty(Constants.Http.User.PARAM_EMAIL, user.getEmail());
        data.addProperty(Constants.Http.User.PARAM_CITY, user.getCity());
        data.addProperty(Constants.Http.User.PARAM_AREA, user.getArea());
        data.addProperty(Constants.Http.User.PARAM_ABOUT, user.getAbout());
        return getUserService().updateById(user.getObjectId(), data);
    }

    public JsonObject updateUserScreenName(String userId, String screenName) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.User.PARAM_SCREEN_NAME, screenName);
        return getUserService().updateById(userId, data);
    }

    public JsonObject updateUserEmail(String userId, String email) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.User.PARAM_EMAIL, email);
        return getUserService().updateById(userId, data);
    }

    public JsonObject updateUserCity(String userId, String city) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.User.PARAM_CITY, city);
        return getUserService().updateById(userId, data);
    }

    public JsonObject updateUserArea(String userId, String area) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.User.PARAM_AREA, area);
        return getUserService().updateById(userId, data);
    }

    public JsonObject updateUserAbout(String userId, String about) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.User.PARAM_ABOUT, about);
        return getUserService().updateById(userId, data);
    }

    public JsonObject deleteParty(String partyId) {
        final JsonObject data = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        data.addProperty("deletedAt", now.toString(Constants.DateTime.FORMAT));
        return getPartyService().updateById(partyId, data);
    }

    private JsonObject buildPartyCommentRequestJson(String commentId) {
        final JsonObject body = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        body.addProperty("deletedAt", now.toString(Constants.DateTime.FORMAT));
        String path = Constants.Http.URL_PARTY_COMMENTS_FRAG + "/" + commentId;
        return buildRequestJson(path, body);
    }

    private JsonObject buildPartyCommentsRequestJson(String partyId, String commentId) {
        final JsonObject body = new JsonObject();
        final JsonArray partyComments = new JsonArray();
        partyComments.add(new JsonPrimitive(commentId));
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", "Remove");
        opJson.add("objects", partyComments);
        body.add(Constants.Http.Party.PARAM_COMMENTS, opJson);
        String path = Constants.Http.URL_PARTIES_FRAG + "/" + partyId;
        return buildRequestJson(path, body);
    }

    public JsonArray deletePartyComment(String commentId, String partyId) {
        final JsonObject script = new JsonObject();
        final JsonArray requests = new JsonArray();
        requests.add(buildPartyCommentRequestJson(commentId));
        requests.add(buildPartyCommentsRequestJson(partyId, commentId));
        script.add(Constants.Http.Batch.PARAM_REQUESTS, requests);
        return getBatchService().execute(script);
    }

    public JsonObject addPartyCommentLike(String commentId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updatePartyCommentLikes(op, commentId, userId);
    }

    public JsonObject removePartyCommentLike(String commentId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updatePartyCommentLikes(op, commentId, userId);
    }

    private JsonObject updatePartyCommentLikes(JsonObject op, String commentId, String userId) {
        final JsonObject data = new JsonObject();
        final JsonArray partyCommentLikes = new JsonArray();
        partyCommentLikes.add(new JsonPrimitive(userId));
        op.add("objects", partyCommentLikes);
        data.add(Constants.Http.PartyComment.PARAM_LIKES, op);
        return getPartyCommentService().updateById(commentId, data);
    }

    public PartyReason newPartyReason(PartyReason reason) {
        return getPartyReasonService().newPartyReason(reason);
    }

    public List<PartyReason> getPartyReasons(List<String> idList) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        String where = whereJson.toString();
        return getPartyReasonService().getList("-createdAt", where).getResults();
    }

    public JsonObject updateUserChatId(String userId, String chatId) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.User.PARAM_CHAT_ID, chatId);
        return getUserService().updateById(userId, data);
    }

    public List<Asking> getAskingListAfter(int category, String after, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty("category", category);
        if (after != null) {
            whereJson.add("updatedAt", buildDateTimeAfterJson(after));
        }
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getAskingService().getList("-updatedAt", where, limit).getResults();
    }

    public List<Asking> getAskingListBefore(int category, String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty("category", category);
        whereJson.add("updatedAt", buildDateTimeBeforeJson(before));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getAskingService().getList("-updatedAt", where, limit).getResults();
    }

    public List<Asking> getAskingListBefore(List<String> idList, String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        whereJson.add("updatedAt", buildDateTimeBeforeJson(before));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getAskingService().getList("-updatedAt", where, limit).getResults();
    }

    public Asking newAsking(Asking asking) {
        Gson gson = new Gson();
        JsonObject data = gson.toJsonTree(asking).getAsJsonObject();
        return getAskingService().newAsking(data);
    }

    public JsonObject addUserAsking(String userId, String askingId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateUserAskingList(op, userId, askingId);
    }

    private JsonObject updateUserAskingList(JsonObject op, String userId, String askingId) {
        final JsonObject data = new JsonObject();
        final JsonArray userAskingList = new JsonArray();
        userAskingList.add(new JsonPrimitive(askingId));
        op.add("objects", userAskingList);
        data.add(Constants.Http.User.PARAM_ASKINGS, op);
        return getUserService().updateById(userId, data);
    }

    public Asking getAskingById(String id) {
        return getAskingService().getById(id);
    }

    public List<AskingReply> getAskingReplies(List<String> idList, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getAskingReplyService().getList("-createdAt", where, limit).getResults();
    }

    public List<AskingReply> getAskingRepliesBefore(List<String> idList, String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        whereJson.add("createdAt", buildDateTimeBeforeJson(before));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getAskingReplyService().getList("-createdAt", where, limit).getResults();
    }

    public AskingReply newAskingReply(AskingReply askingReply) {
        Gson gson = new Gson();
        JsonObject data = gson.toJsonTree(askingReply).getAsJsonObject();
        return getAskingReplyService().newAskingReply(data);
    }

    public JsonObject addAskingReplies(String askingId, String replyId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateAskingReplies(op, askingId, replyId);
    }

    private JsonObject updateAskingReplies(JsonObject op, String askingId, String replyId) {
        final JsonObject data = new JsonObject();
        final JsonArray askingReplies = new JsonArray();
        askingReplies.add(new JsonPrimitive(replyId));
        op.add("objects", askingReplies);
        data.add(Constants.Http.Asking.PARAM_REPLIES, op);
        return getAskingService().updateById(askingId, data);
    }

    public List<Asking> getAskingList(List<String> idList, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getAskingService().getList("-updatedAt", where, limit).getResults();
    }

    public JsonObject deleteAsking(String askingId) {
        final JsonObject data = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        data.addProperty("deletedAt", now.toString(Constants.DateTime.FORMAT));
        return getAskingService().updateById(askingId, data);
    }

    public JsonArray addPartyFavorite(String partyId, String userId) {
        return updatePartyFavorites("AddUnique", partyId, userId);
    }

    public JsonArray removePartyFavorite(String partyId, String userId) {
        return updatePartyFavorites("Remove", partyId, userId);
    }

    private JsonObject buildUserPartyFavoritesRequestJson(String op, String userId, String partyId) {
        final JsonObject body = new JsonObject();
        final JsonArray userPartyFavorites = new JsonArray();
        userPartyFavorites.add(new JsonPrimitive(partyId));
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", op);
        opJson.add("objects", userPartyFavorites);
        body.add(Constants.Http.User.PARAM_PARTY_FAVORITES, opJson);
        String path = Constants.Http.URL_USERS_FRAG + "/" + userId;
        return buildRequestJson(path, body);
    }

    private JsonObject buildPartyFavoritesRequestJson(String op, String partyId, String userId) {
        final JsonObject body = new JsonObject();
        final JsonArray partyFavorites = new JsonArray();
        partyFavorites.add(new JsonPrimitive(userId));
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", op);
        opJson.add("objects", partyFavorites);
        body.add(Constants.Http.Party.PARAM_FAVORITES, opJson);
        String path = Constants.Http.URL_PARTIES_FRAG + "/" + partyId;
        return buildRequestJson(path, body);
    }

    private JsonArray updatePartyFavorites(String op, String partyId, String userId) {
        final JsonObject script = new JsonObject();
        final JsonArray requests = new JsonArray();
        requests.add(buildUserPartyFavoritesRequestJson(op, userId, partyId));
        requests.add(buildPartyFavoritesRequestJson(op, partyId, userId));
        script.add(Constants.Http.Batch.PARAM_REQUESTS, requests);
        return getBatchService().execute(script);
    }

    public JsonObject addAskingLike(String askingId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateAskingLikes(op, askingId, userId);
    }

    public JsonObject removeAskingLike(String askingId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateAskingLikes(op, askingId, userId);
    }

    private JsonObject updateAskingLikes(JsonObject op, String askingId, String userId) {
        final JsonObject data = new JsonObject();
        final JsonArray askingLikes = new JsonArray();
        askingLikes.add(new JsonPrimitive(userId));
        op.add("objects", askingLikes);
        data.add(Constants.Http.Asking.PARAM_LIKES, op);
        return getAskingService().updateById(askingId, data);
    }

    public JsonArray addAskingFavorite(String askingId, String userId) {
        return updateAskingFavorites("AddUnique", askingId, userId);
    }

    public JsonArray removeAskingFavorite(String askingId, String userId) {
        return updateAskingFavorites("Remove", askingId, userId);
    }

    private JsonObject buildUserAskingFavoritesRequestJson(String op, String userId, String askingId) {
        final JsonObject body = new JsonObject();
        final JsonArray userAskingFavorites = new JsonArray();
        userAskingFavorites.add(new JsonPrimitive(askingId));
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", op);
        opJson.add("objects", userAskingFavorites);
        body.add(Constants.Http.User.PARAM_ASKING_FAVORITES, opJson);
        String path = Constants.Http.URL_USERS_FRAG + "/" + userId;
        return buildRequestJson(path, body);
    }

    private JsonObject buildAskingFavoritesRequestJson(String op, String askingId, String userId) {
        final JsonObject body = new JsonObject();
        final JsonArray askingFavorites = new JsonArray();
        askingFavorites.add(new JsonPrimitive(userId));
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", op);
        opJson.add("objects", askingFavorites);
        body.add(Constants.Http.Asking.PARAM_FAVORITES, opJson);
        String path = Constants.Http.URL_ASKINGS_FRAG + "/" + askingId;
        return buildRequestJson(path, body);
    }

    private JsonArray updateAskingFavorites(String op, String askingId, String userId) {
        final JsonObject script = new JsonObject();
        final JsonArray requests = new JsonArray();
        requests.add(buildUserAskingFavoritesRequestJson(op, userId, askingId));
        requests.add(buildAskingFavoritesRequestJson(op, askingId, userId));
        script.add(Constants.Http.Batch.PARAM_REQUESTS, requests);
        return getBatchService().execute(script);
    }

    public JsonObject addAskingReplyLike(String askingReplyId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateAskingReplyLikes(op, askingReplyId, userId);
    }

    public JsonObject removeAskingReplyLike(String askingReplyId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateAskingReplyLikes(op, askingReplyId, userId);
    }

    private JsonObject updateAskingReplyLikes(JsonObject op, String askingReplyId, String userId) {
        final JsonObject data = new JsonObject();
        final JsonArray askingReplyLikes = new JsonArray();
        askingReplyLikes.add(new JsonPrimitive(userId));
        op.add("objects", askingReplyLikes);
        data.add(Constants.Http.AskingReply.PARAM_LIKES, op);
        return getAskingReplyService().updateById(askingReplyId, data);
    }

    private JsonObject buildAskingReplyRequestJson(String askingReplyId) {
        final JsonObject body = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        body.addProperty("deletedAt", now.toString(Constants.DateTime.FORMAT));
        String path = Constants.Http.URL_ASKING_REPLIES_FRAG + "/" + askingReplyId;
        return buildRequestJson(path, body);
    }

    private JsonObject buildAskingRepliesRequestJson(String askingId, String askingReplyId) {
        final JsonObject body = new JsonObject();
        final JsonArray askingReplies = new JsonArray();
        askingReplies.add(new JsonPrimitive(askingReplyId));
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", "Remove");
        opJson.add("objects", askingReplies);
        body.add(Constants.Http.Asking.PARAM_REPLIES, opJson);
        String path = Constants.Http.URL_ASKINGS_FRAG + "/" + askingId;
        return buildRequestJson(path, body);
    }

    public JsonArray deleteAskingReply(String askingReplyId, String askingId) {
        final JsonObject script = new JsonObject();
        final JsonArray requests = new JsonArray();
        requests.add(buildAskingReplyRequestJson(askingReplyId));
        requests.add(buildAskingRepliesRequestJson(askingId, askingReplyId));
        script.add(Constants.Http.Batch.PARAM_REQUESTS, requests);
        return getBatchService().execute(script);
    }

    public HashMap<String, String> getInAppContactList(List<String> numberList) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add(Constants.Http.PARAM_USERNAME, buildIdListJson(numberList));
        String where = whereJson.toString();
        String keys = Constants.Http.PARAM_USERNAME + ",objectId";
        List<JsonObject> result = getUserService().getList(keys, where).getResults();
        HashMap<String, String> contactList = new HashMap<String, String>();
        for (JsonObject jsonObject: result) {
            String phone = jsonObject.get(Constants.Http.PARAM_USERNAME).getAsString();
            String userId = jsonObject.get("objectId").getAsString();
            contactList.put(phone, userId);
        }
        return contactList;
    }

    public Report newReport(Report report) {
        Gson gson = new Gson();
        JsonObject data = gson.toJsonTree(report).getAsJsonObject();
        return getReportService().newReport(data);
    }

    public Feedback newFeedback(Feedback feedback) {
        Gson gson = new Gson();
        JsonObject data = gson.toJsonTree(feedback).getAsJsonObject();
        return getFeedbackService().newFeedback(data);
    }
}