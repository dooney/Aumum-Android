
package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.model.Area;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.model.AskingBoard;
import com.aumum.app.mobile.core.model.AskingGroup;
import com.aumum.app.mobile.core.model.AskingReply;
import com.aumum.app.mobile.core.model.CityGroup;
import com.aumum.app.mobile.core.model.CreditGift;
import com.aumum.app.mobile.core.model.CreditOrder;
import com.aumum.app.mobile.core.model.CreditRule;
import com.aumum.app.mobile.core.model.EventCategory;
import com.aumum.app.mobile.core.model.Feed;
import com.aumum.app.mobile.core.model.Feedback;
import com.aumum.app.mobile.core.model.Game;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.MomentComment;
import com.aumum.app.mobile.core.model.PartyComment;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.PartyReason;
import com.aumum.app.mobile.core.model.PartyRequest;
import com.aumum.app.mobile.core.model.PlaceRange;
import com.aumum.app.mobile.core.model.Report;
import com.aumum.app.mobile.core.model.Special;
import com.aumum.app.mobile.core.model.SpecialProduct;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserTag;
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

    private AreaService getAreaService() {
        return getRestAdapter().create(AreaService.class);
    }

    private CityGroupService getCityGroupService() {
        return getRestAdapter().create(CityGroupService.class);
    }

    private UserTagService getUserTagService() {
        return getRestAdapter().create(UserTagService.class);
    }

    private MomentService getMomentService() {
        return getRestAdapter().create(MomentService.class);
    }

    private MomentCommentService getMomentCommentService() {
        return getRestAdapter().create(MomentCommentService.class);
    }

    private SpecialService getSpecialService() {
        return getRestAdapter().create(SpecialService.class);
    }

    private SpecialProductService getSpecialProductService() {
        return getRestAdapter().create(SpecialProductService.class);
    }

    private PartyRequestService getPartyRequestService() {
        return getRestAdapter().create(PartyRequestService.class);
    }

    private FeedService getFeedService() {
        return getRestAdapter().create(FeedService.class);
    }

    private EventCategoryService getEventCategoryService() {
        return getRestAdapter().create(EventCategoryService.class);
    }

    private GameService getGameService() {
        return getRestAdapter().create(GameService.class);
    }

    private CreditGiftService getCreditGiftService() {
        return getRestAdapter().create(CreditGiftService.class);
    }

    private CreditOrderService getCreditOrderService() {
        return getRestAdapter().create(CreditOrderService.class);
    }

    private AskingGroupService getAskingGroupService() {
        return getRestAdapter().create(AskingGroupService.class);
    }

    private AskingBoardService getAskingBoardService() {
        return getRestAdapter().create(AskingBoardService.class);
    }

    private CreditRuleService getCreditRuleService() {
        return getRestAdapter().create(CreditRuleService.class);
    }

    private RestAdapter getRestAdapter() {
        return restAdapter;
    }

    public User authenticate(String username, String password) {
        return getUserService().authenticate(username, password);
    }

    private JsonObject buildListJson(String op, List<String> idList) {
        final JsonArray idListJson = new JsonArray();
        for (String id: idList) {
            idListJson.add(new JsonPrimitive(id));
        }
        final JsonObject objectIdInJson = new JsonObject();
        objectIdInJson.add(op, idListJson);
        return objectIdInJson;
    }

    private JsonObject buildIdListJson(List<String> idList) {
        return buildListJson("$in", idList);
    }

    private JsonObject buildNotIdListJson(List<String> idList) {
        return buildListJson("$nin", idList);
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
        List<User> result = getUserService().getByScreenName(where, 1).getResults();
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public User getUserByChatId(String id) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty("chatId", id);
        String where = whereJson.toString();
        List<User> result = getUserService().getList(where, 1).getResults();
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public List<User> getGroupUsers(List<String> chatIds) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("chatId", buildIdListJson(chatIds));
        String where = whereJson.toString();
        return getUserService().getList(where, Integer.MAX_VALUE).getResults();
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

    private JsonObject buildAreaUsersJson(String userId, String area) {
        final JsonObject areaUsersJson = new JsonObject();
        final JsonObject userIdJson = new JsonObject();
        userIdJson.addProperty("$ne", userId);
        areaUsersJson.add("objectId", userIdJson);
        areaUsersJson.addProperty("area", area);
        return areaUsersJson;
    }

    public List<User> getAreaUsers(String userId, String area) {
        final JsonObject whereJson = buildAreaUsersJson(userId, area);
        String where = whereJson.toString();
        return getUserService().getList(where, Integer.MAX_VALUE).getResults();
    }

    public int getAreaUsersCount(String userId, String area) {
        final JsonObject whereJson = buildAreaUsersJson(userId, area);
        String where = whereJson.toString();
        JsonObject result = getUserService().getCount(where, 1, 0);
        return result.get("count").getAsInt();
    }

    private JsonObject buildTagUsersJson(String userId, List<String> tags) {
        final JsonObject tagUsersJson = new JsonObject();
        final JsonObject userIdJson = new JsonObject();
        userIdJson.addProperty("$ne", userId);
        tagUsersJson.add("objectId", userIdJson);
        if (tags.size() > 1) {
            final JsonArray tagsJson = new JsonArray();
            for(String tag: tags) {
                final JsonObject tagJson = new JsonObject();
                tagJson.addProperty("tags", tag);
                tagsJson.add(tagJson);
            }
            tagUsersJson.add("$or", tagsJson);
        } else {
            tagUsersJson.addProperty("tags", tags.get(0));
        }
        return tagUsersJson;
    }

    public List<User> getTagUsers(String userId, List<String> tags) {
        final JsonObject whereJson = buildTagUsersJson(userId, tags);
        String where = whereJson.toString();
        return getUserService().getList(where, Integer.MAX_VALUE).getResults();
    }

    public int getTagUsersCount(String userId, List<String> tags) {
        final JsonObject whereJson = buildTagUsersJson(userId, tags);
        String where = whereJson.toString();
        JsonObject result = getUserService().getCount(where, 1, 0);
        return result.get("count").getAsInt();
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
        JsonArray tags = new JsonArray();
        for (String tag: user.getTags()) {
            tags.add(new JsonPrimitive(tag));
        }
        data.add(Constants.Http.User.PARAM_TAGS, tags);
        data.addProperty(Constants.Http.User.PARAM_ABOUT, user.getAbout());
        data.addProperty(Constants.Http.User.PARAM_CHAT_ID, user.getChatId());
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

    public JsonObject updateUserTags(String userId, List<String> tags) {
        JsonArray array = new JsonArray();
        for (String tag: tags) {
            array.add(new JsonPrimitive(tag));
        }
        final JsonObject data = new JsonObject();
        data.add(Constants.Http.User.PARAM_TAGS, array);
        return getUserService().updateById(userId, data);
    }

    public JsonObject updateUserAbout(String userId, String about) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.User.PARAM_ABOUT, about);
        return getUserService().updateById(userId, data);
    }

    public JsonObject updateUserCredit(String userId, int credit) {
        if (credit != 0) {
            final JsonObject data = new JsonObject();
            final JsonObject opJson = new JsonObject();
            opJson.addProperty("__op", "Increment");
            opJson.addProperty("amount", credit);
            data.add(Constants.Http.User.PARAM_CREDIT, opJson);
            return getUserService().updateById(userId, data);
        }
        return null;
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

    public List<Asking> getAskingListAfter(String groupId, String after, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty("groupId", groupId);
        if (after != null) {
            whereJson.add("updatedAt", buildDateTimeAfterJson(after));
        }
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getAskingService().getList("-updatedAt", where, limit).getResults();
    }

    public List<Asking> getAskingListBefore(String groupId, String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty("groupId", groupId);
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

    public List<String> getAskingUnreadGroups(List<String> idList, String after) {
        final JsonObject whereJson = new JsonObject();
        if (after != null) {
            whereJson.add("createdAt", buildDateTimeAfterJson(after));
        }
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt", liveJson);
        whereJson.add("groupId", buildIdListJson(idList));
        String where = whereJson.toString();
        List<Asking> askingList = getAskingService().getUnread(where, "groupId").getResults();
        ArrayList<String> groups = new ArrayList<>();
        for (Asking asking: askingList) {
            groups.add(asking.getGroupId());
        }
        return groups;
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

    public JsonObject addUserAskingGroup(String userId, String askingGroupId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateUserAskingGroupList(op, userId, askingGroupId);
    }

    public JsonObject removeUserAskingGroup(String userId, String askingGroupId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateUserAskingGroupList(op, userId, askingGroupId);
    }

    private JsonObject updateUserAskingGroupList(JsonObject op, String userId, String askingGroupId) {
        final JsonObject data = new JsonObject();
        final JsonArray userAskingGroupList = new JsonArray();
        userAskingGroupList.add(new JsonPrimitive(askingGroupId));
        op.add("objects", userAskingGroupList);
        data.add(Constants.Http.User.PARAM_ASKING_GROUPS, op);
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

    public List<Asking> getAskingList(List<String> idList, boolean excludesAnonymous, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        if (excludesAnonymous) {
            whereJson.addProperty("isAnonymous", false);
        }
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

    public List<Area> getAreaListByCity(int cityId) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty("city", cityId);
        String where = whereJson.toString();
        return getAreaService().getList(where, Integer.MAX_VALUE).getResults();
    }

    public CityGroup getCityGroup(int cityId) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty("city", cityId);
        String where = whereJson.toString();
        List<CityGroup> result = getCityGroupService().getList(where, 1).getResults();
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public List<UserTag> getUserTags() {
        return getUserTagService().getList().getResults();
    }

    public List<Moment> getMomentsAfter(String after, int limit) {
        final JsonObject whereJson = new JsonObject();
        if (after != null) {
            whereJson.add("createdAt", buildDateTimeAfterJson(after));
        }
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt", liveJson);
        String where = whereJson.toString();
        return getMomentService().getList("-createdAt", where, limit).getResults();
    }

    private List<Moment> getMomentsBeforeCore(JsonObject whereJson, String before, int limit) {
        whereJson.add("createdAt", buildDateTimeBeforeJson(before));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getMomentService().getList("-createdAt", where, limit).getResults();
    }

    public List<Moment> getMomentsBefore(String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        return getMomentsBeforeCore(whereJson, before, limit);
    }

    public Moment newMoment(Moment moment) {
        Gson gson = new Gson();
        JsonObject data = gson.toJsonTree(moment).getAsJsonObject();
        return getMomentService().newMoment(data);
    }

    public JsonObject addUserMoment(String userId, String momentId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateUserMomentList(op, userId, momentId);
    }

    private JsonObject updateUserMomentList(JsonObject op, String userId, String momentId) {
        final JsonObject data = new JsonObject();
        final JsonArray userMomentList = new JsonArray();
        userMomentList.add(new JsonPrimitive(momentId));
        op.add("objects", userMomentList);
        data.add(Constants.Http.User.PARAM_MOMENTS, op);
        return getUserService().updateById(userId, data);
    }

    public JsonObject addMomentLike(String momentId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateMomentLikes(op, momentId, userId);
    }

    public JsonObject removeMomentLike(String momentId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateMomentLikes(op, momentId, userId);
    }

    private JsonObject updateMomentLikes(JsonObject op, String momentId, String userId) {
        final JsonObject data = new JsonObject();
        final JsonArray momentLikes = new JsonArray();
        momentLikes.add(new JsonPrimitive(userId));
        op.add("objects", momentLikes);
        data.add(Constants.Http.Moment.PARAM_LIKES, op);
        return getMomentService().updateById(momentId, data);
    }

    public Moment getMomentById(String id) {
        return getMomentService().getById(id);
    }

    public JsonObject deleteMoment(String momentId) {
        final JsonObject data = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        data.addProperty("deletedAt", now.toString(Constants.DateTime.FORMAT));
        return getMomentService().updateById(momentId, data);
    }

    public JsonObject addMomentCommentLike(String commentId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateMomentCommentLikes(op, commentId, userId);
    }

    public JsonObject removeMomentCommentLike(String commentId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateMomentCommentLikes(op, commentId, userId);
    }

    private JsonObject updateMomentCommentLikes(JsonObject op, String commentId, String userId) {
        final JsonObject data = new JsonObject();
        final JsonArray momentCommentLikes = new JsonArray();
        momentCommentLikes.add(new JsonPrimitive(userId));
        op.add("objects", momentCommentLikes);
        data.add(Constants.Http.MomentComment.PARAM_LIKES, op);
        return getMomentCommentService().updateById(commentId, data);
    }

    public List<MomentComment> getMomentComments(List<String> idList) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getMomentCommentService().getList("-createdAt", where).getResults();
    }

    public MomentComment newMomentComment(MomentComment comment) {
        return getMomentCommentService().newMomentComment(comment);
    }

    public JsonObject addMomentComment(String partyId, String commentId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateMomentComments(op, partyId, commentId);
    }

    private JsonObject buildMomentCommentRequestJson(String commentId) {
        final JsonObject body = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        body.addProperty("deletedAt", now.toString(Constants.DateTime.FORMAT));
        String path = Constants.Http.URL_MOMENT_COMMENTS_FRAG + "/" + commentId;
        return buildRequestJson(path, body);
    }

    private JsonObject buildMomentCommentsRequestJson(String momentId, String commentId) {
        final JsonObject body = new JsonObject();
        final JsonArray momentComments = new JsonArray();
        momentComments.add(new JsonPrimitive(commentId));
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", "Remove");
        opJson.add("objects", momentComments);
        body.add(Constants.Http.Moment.PARAM_COMMENTS, opJson);
        String path = Constants.Http.URL_MOMENTS_FRAG + "/" + momentId;
        return buildRequestJson(path, body);
    }

    public JsonArray deleteMomentComment(String commentId, String momentId) {
        final JsonObject script = new JsonObject();
        final JsonArray requests = new JsonArray();
        requests.add(buildMomentCommentRequestJson(commentId));
        requests.add(buildMomentCommentsRequestJson(momentId, commentId));
        script.add(Constants.Http.Batch.PARAM_REQUESTS, requests);
        return getBatchService().execute(script);
    }

    private JsonObject updateMomentComments(JsonObject op, String momentId, String commentId) {
        final JsonObject data = new JsonObject();
        final JsonArray momentComments = new JsonArray();
        momentComments.add(new JsonPrimitive(commentId));
        op.add("objects", momentComments);
        data.add(Constants.Http.Moment.PARAM_COMMENTS, op);
        return getMomentService().updateById(momentId, data);
    }

    public List<Moment> getMoments(List<String> idList, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt", liveJson);
        String where = whereJson.toString();
        return getMomentService().getList("-createdAt", where, limit).getResults();
    }

    public List<Moment> getMomentsBefore(List<String> idList, String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        return getMomentsBeforeCore(whereJson, before, limit);
    }

    public int getMomentsCountAfter(String after) {
        final JsonObject whereJson = new JsonObject();
        if (after != null) {
            whereJson.add("createdAt", buildDateTimeAfterJson(after));
        }
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        JsonObject result = getMomentService().getCount(where, 1, 0);
        return result.get("count").getAsInt();
    }

    public List<Special> getSpecialList() {
        return getSpecialService().getList().getResults();
    }

    public List<SpecialProduct> getSpecialProductList(String specialId) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty("specialId", specialId);
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getSpecialProductService().getList("-now", where, Integer.MAX_VALUE).getResults();
    }

    public JsonArray addSpecialProductLike(String id, String specialId, String userId) {
        return updateSpecialProductLikes("AddUnique", id, specialId, userId);
    }

    public JsonArray removeSpecialProductLike(String id, String specialId, String userId) {
        return updateSpecialProductLikes("Remove", id, specialId, userId);
    }

    private JsonObject buildSpecialLikesRequestJson(String op, String specialId) {
        final JsonObject body = new JsonObject();
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", "Increment");
        if (op.equals("Remove")) {
            opJson.addProperty("amount", -1);
        } else {
            opJson.addProperty("amount", 1);
        }
        body.add(Constants.Http.Special.PARAM_LIKES, opJson);
        String path = Constants.Http.URL_SPECIALS_FRAG + "/" + specialId;
        return buildRequestJson(path, body);
    }

    private JsonObject buildSpecialProductLikesRequestJson(String op, String id, String userId) {
        final JsonObject body = new JsonObject();
        final JsonArray specialProductLikes = new JsonArray();
        specialProductLikes.add(new JsonPrimitive(userId));
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", op);
        opJson.add("objects", specialProductLikes);
        body.add(Constants.Http.SpecialProduct.PARAM_LIKES, opJson);
        String path = Constants.Http.URL_SPECIAL_PRODUCTS_FRAG + "/" + id;
        return buildRequestJson(path, body);
    }

    private JsonArray updateSpecialProductLikes(String op, String id, String specialId, String userId) {
        final JsonObject script = new JsonObject();
        final JsonArray requests = new JsonArray();
        requests.add(buildSpecialLikesRequestJson(op, specialId));
        requests.add(buildSpecialProductLikesRequestJson(op, id, userId));
        script.add(Constants.Http.Batch.PARAM_REQUESTS, requests);
        return getBatchService().execute(script);
    }

    public JsonArray addSpecialProductFavorite(String id, String userId) {
        return updateSpecialProductFavorites("AddUnique", id, userId);
    }

    public JsonArray removeSpecialProductFavorite(String id, String userId) {
        return updateSpecialProductFavorites("Remove", id, userId);
    }

    private JsonObject buildUserSpecialFavoritesRequestJson(String op, String userId, String id) {
        final JsonObject body = new JsonObject();
        final JsonArray userSpecialProductFavorites = new JsonArray();
        userSpecialProductFavorites.add(new JsonPrimitive(id));
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", op);
        opJson.add("objects", userSpecialProductFavorites);
        body.add(Constants.Http.User.PARAM_SPECIAL_FAVORITES, opJson);
        String path = Constants.Http.URL_USERS_FRAG + "/" + userId;
        return buildRequestJson(path, body);
    }

    private JsonObject buildSpecialProductFavoritesRequestJson(String op, String id, String userId) {
        final JsonObject body = new JsonObject();
        final JsonArray specialProductFavorites = new JsonArray();
        specialProductFavorites.add(new JsonPrimitive(userId));
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", op);
        opJson.add("objects", specialProductFavorites);
        body.add(Constants.Http.SpecialProduct.PARAM_FAVORITES, opJson);
        String path = Constants.Http.URL_SPECIAL_PRODUCTS_FRAG + "/" + id;
        return buildRequestJson(path, body);
    }

    private JsonArray updateSpecialProductFavorites(String op, String id, String userId) {
        final JsonObject script = new JsonObject();
        final JsonArray requests = new JsonArray();
        requests.add(buildUserSpecialFavoritesRequestJson(op, userId, id));
        requests.add(buildSpecialProductFavoritesRequestJson(op, id, userId));
        script.add(Constants.Http.Batch.PARAM_REQUESTS, requests);
        return getBatchService().execute(script);
    }

    public List<SpecialProduct> getFavoriteProductList(List<String> idList, String specialId) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        whereJson.addProperty("specialId", specialId);
        String where = whereJson.toString();
        return getSpecialProductService().getList("-now", where, Integer.MAX_VALUE).getResults();
    }

    public List<PartyRequest> getPartyRequestsAfter(String after, int limit) {
        final JsonObject whereJson = new JsonObject();
        if (after != null) {
            whereJson.add("createdAt", buildDateTimeAfterJson(after));
        }
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt", liveJson);
        String where = whereJson.toString();
        return getPartyRequestService().getList("-createdAt", where, limit).getResults();
    }

    public List<PartyRequest> getPartyRequestsBefore(String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("createdAt", buildDateTimeBeforeJson(before));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getPartyRequestService().getList("-createdAt", where, limit).getResults();
    }

    public PartyRequest newPartyRequest(PartyRequest partyRequest) {
        Gson gson = new Gson();
        JsonObject data = gson.toJsonTree(partyRequest).getAsJsonObject();
        return getPartyRequestService().newPartyRequest(data);
    }

    public JsonObject deletePartyRequest(String partyRequestId) {
        final JsonObject data = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        data.addProperty("deletedAt", now.toString(Constants.DateTime.FORMAT));
        return getPartyRequestService().updateById(partyRequestId, data);
    }

    public int getPartyRequestsCountAfter(String after) {
        final JsonObject whereJson = new JsonObject();
        if (after != null) {
            whereJson.add("createdAt", buildDateTimeAfterJson(after));
        }
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        JsonObject result = getPartyRequestService().getCount(where, 1, 0);
        return result.get("count").getAsInt();
    }

    public List<Feed> getFeedList(int type) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty("type", type);
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getFeedService().getList("seq", where, Integer.MAX_VALUE).getResults();
    }

    public List<EventCategory> getEventCategoryList() {
        final JsonObject whereJson = new JsonObject();
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getEventCategoryService().getList("seq", where, Integer.MAX_VALUE).getResults();
    }

    public List<Game> getGameList() {
        final JsonObject whereJson = new JsonObject();
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getGameService().getList("seq", where, Integer.MAX_VALUE).getResults();
    }

    public List<CreditGift> getCreditGiftList() {
        final JsonObject whereJson = new JsonObject();
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getCreditGiftService().getList("seq", where, Integer.MAX_VALUE).getResults();
    }

    public CreditOrder newCreditOrder(CreditOrder creditOrder) {
        return getCreditOrderService().newCreditOrder(creditOrder);
    }

    public List<AskingGroup> getAskingGroupList(List<String> idList) {
        final JsonObject whereJson = new JsonObject();
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt", liveJson);
        whereJson.add("objectId", buildIdListJson(idList));
        String where = whereJson.toString();
        return getAskingGroupService().getList("seq", where, Integer.MAX_VALUE).getResults();
    }

    public List<AskingGroup> getRecommendAskingGroupList(List<String> keywords, List<String> excludes) {
        final JsonObject whereJson = new JsonObject();
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt", liveJson);
        final JsonArray keywordJson = new JsonArray();
        final JsonObject keywordListJson = new JsonObject();
        keywordListJson.add("keyword", buildIdListJson(keywords));
        keywordJson.add(keywordListJson);
        final JsonObject keywordLiveJson = new JsonObject();
        keywordLiveJson.add("keyword", liveJson);
        keywordJson.add(keywordLiveJson);
        whereJson.add("$or", keywordJson);
        whereJson.add("objectId", buildNotIdListJson(excludes));
        String where = whereJson.toString();
        return getAskingGroupService().getList("seq", where, Integer.MAX_VALUE).getResults();
    }

    public List<AskingBoard> getAskingBoardList() {
        final JsonObject whereJson = new JsonObject();
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getAskingBoardService().getList("seq", where, Integer.MAX_VALUE).getResults();
    }

    public List<AskingGroup> getAskingGroupListByBoardId(String boardId) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty("boardId", boardId);
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt", liveJson);
        String where = whereJson.toString();
        return getAskingGroupService().getList("seq", where, Integer.MAX_VALUE).getResults();
    }

    public List<CreditRule> getCreditRuleList() {
        final JsonObject whereJson = new JsonObject();
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getCreditRuleService().getList("seq", where, Integer.MAX_VALUE).getResults();
    }
}