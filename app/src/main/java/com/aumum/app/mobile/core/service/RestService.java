
package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.model.AskingReply;
import com.aumum.app.mobile.core.model.Comment;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Message;
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

    private AskingService getAskingService() {
        return getRestAdapter().create(AskingService.class);
    }

    private AskingReplyService getAskingReplyService() {
        return getRestAdapter().create(AskingReplyService.class);
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

    public User register(String email, String password, String screenName, int area) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.PARAM_USERNAME, email);
        data.addProperty(Constants.Http.PARAM_PASSWORD, password);
        data.addProperty(Constants.Http.PARAM_EMAIL, email);
        data.addProperty(Constants.Http.PARAM_SCREEN_NAME, screenName);
        data.addProperty(Constants.Http.PARAM_AREA, area);
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
        return getPartyService().getList("-createdAt", where, limit).getResults();
    }

    public List<Party> getPartiesAfter(String after, int limit) {
        final JsonObject whereJson = new JsonObject();
        return getPartiesAfterCore(whereJson, after, limit);
    }

    private List<Party> getPartiesBeforeCore(JsonObject whereJson, String before, int limit) {
        whereJson.add("createdAt", buildDateTimeBeforeJson(before));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getPartyService().getList("-createdAt", where, limit).getResults();
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

    public List<Party> getParties(List<String> idList, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
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
        return getUserService().getByChatId(where, 1).getResults().get(0);
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
        return getUserService().updateById(userId, data);
    }

    public List<Message> getMessagesAfter(List<String> idList, List<Integer> typeList, String after, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        if (typeList != null) {
            final JsonArray typeListJson = new JsonArray();
            for (int type : typeList) {
                typeListJson.add(new JsonPrimitive(type));
            }
            final JsonObject typeInJson = new JsonObject();
            typeInJson.add("$in", typeListJson);
            whereJson.add("type", typeInJson);
        }
        if (after != null) {
            whereJson.add("createdAt", buildDateTimeAfterJson(after));
        }
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

    public List<Comment> getPartyComments(List<String> idList) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        String where = whereJson.toString();
        return getPartyCommentService().getList("-createdAt", where).getResults();
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

    public JsonObject deletePartyComment(String commentId) {
        final JsonObject data = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        data.addProperty("deletedAt", now.toString(Constants.DateTime.FORMAT));
        return getPartyCommentService().updateById(commentId, data);
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

    public List<PartyReason> getPartyReasons(List<String> idList) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        String where = whereJson.toString();
        return getPartyReasonService().getList("-createdAt", where).getResults();
    }

    public List<Party> getLiveParties() {
        final JsonObject whereJson = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        whereJson.add("dateTime", buildDateTimeAfterJson(now.toString(Constants.DateTime.FORMAT)));
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        whereJson.add("deletedAt" ,liveJson);
        String where = whereJson.toString();
        return getPartyService().getList("dateTime", where, Integer.MAX_VALUE).getResults();
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

    public JsonObject removeAskingReplies(String askingId, String replyId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
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
        String where = whereJson.toString();
        return getAskingService().getList("-updatedAt", where, limit).getResults();
    }

    public JsonObject deleteAsking(String askingId) {
        final JsonObject data = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        data.addProperty("deletedAt", now.toString(Constants.DateTime.FORMAT));
        return getAskingService().updateById(askingId, data);
    }

    public JsonObject addPartyFavorite(String partyId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updatePartyFavorites(op, partyId, userId);
    }

    public JsonObject removePartyFavorite(String partyId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updatePartyFavorites(op, partyId, userId);
    }

    private JsonObject updatePartyFavorites(JsonObject op, String partyId, String userId) {
        final JsonObject data = new JsonObject();
        final JsonArray partyFavorites = new JsonArray();
        partyFavorites.add(new JsonPrimitive(userId));
        op.add("objects", partyFavorites);
        data.add(Constants.Http.Party.PARAM_FAVORITES, op);
        return getPartyService().updateById(partyId, data);
    }

    public JsonObject addUserPartyFavorite(String userId, String partyId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateUserPartyFavorites(op, userId, partyId);
    }

    public JsonObject removeUserPartyFavorite(String userId, String partyId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateUserPartyFavorites(op, userId, partyId);
    }

    private JsonObject updateUserPartyFavorites(JsonObject op, String userId, String partyId) {
        final JsonObject data = new JsonObject();
        final JsonArray userPartyFavorites = new JsonArray();
        userPartyFavorites.add(new JsonPrimitive(partyId));
        op.add("objects", userPartyFavorites);
        data.add(Constants.Http.User.PARAM_PARTY_FAVORITES, op);
        return getUserService().updateById(userId, data);
    }

    public JsonObject addAskingFavorite(String askingId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateAskingFavorites(op, askingId, userId);
    }

    public JsonObject removeAskingFavorite(String askingId, String userId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateAskingFavorites(op, askingId, userId);
    }

    private JsonObject updateAskingFavorites(JsonObject op, String askingId, String userId) {
        final JsonObject data = new JsonObject();
        final JsonArray askingFavorites = new JsonArray();
        askingFavorites.add(new JsonPrimitive(userId));
        op.add("objects", askingFavorites);
        data.add(Constants.Http.Asking.PARAM_FAVORITES, op);
        return getAskingService().updateById(askingId, data);
    }

    public JsonObject addUserAskingFavorite(String userId, String askingId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateUserAskingFavorites(op, userId, askingId);
    }

    public JsonObject removeUserAskingFavorite(String userId, String askingId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "Remove");
        return updateUserAskingFavorites(op, userId, askingId);
    }

    private JsonObject updateUserAskingFavorites(JsonObject op, String userId, String askingId) {
        final JsonObject data = new JsonObject();
        final JsonArray userAskingFavorites = new JsonArray();
        userAskingFavorites.add(new JsonPrimitive(askingId));
        op.add("objects", userAskingFavorites);
        data.add(Constants.Http.User.PARAM_ASKING_FAVORITES, op);
        return getUserService().updateById(userId, data);
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

    public JsonObject deleteAskingReply(String askingReplyId) {
        final JsonObject data = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        data.addProperty("deletedAt", now.toString(Constants.DateTime.FORMAT));
        return getAskingReplyService().updateById(askingReplyId, data);
    }
}