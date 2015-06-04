
package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.model.Area;
import com.aumum.app.mobile.core.model.Comment;
import com.aumum.app.mobile.core.model.Feedback;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.Report;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

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

    private BatchService getBatchService() {
        return getRestAdapter().create(BatchService.class);
    }

    private UserService getUserService() {
        return getRestAdapter().create(UserService.class);
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

    private MomentService getMomentService() {
        return getRestAdapter().create(MomentService.class);
    }

    private MomentCommentService getMomentCommentService() {
        return getRestAdapter().create(MomentCommentService.class);
    }

    private RestAdapter getRestAdapter() {
        return restAdapter;
    }

    public User authenticate(String username, String password) {
        return getUserService().authenticate(username, password);
    }

    private JsonObject opJson(String op, JsonElement value) {
        final JsonObject opJson = new JsonObject();
        opJson.add(op, value);
        return opJson;
    }

    private JsonObject buildListJson(String op, List<String> idList) {
        final JsonArray idListJson = new JsonArray();
        for (String id: idList) {
            idListJson.add(new JsonPrimitive(id));
        }
        return opJson(op, idListJson);
    }

    private JsonObject buildIdListJson(List<String> idList) {
        return buildListJson("$in", idList);
    }

    private JsonObject buildDateTimeJson(String op, String dateTime) {
        final JsonObject timeJson = new JsonObject();
        timeJson.addProperty("__type", "Date");
        timeJson.addProperty("iso", dateTime);
        return opJson(op, timeJson);
    }

    private JsonObject buildDateTimeAfterJson(String dateTime) {
        return buildDateTimeJson("$gt", dateTime);
    }

    private JsonObject buildDateTimeBeforeJson(String dateTime) {
        return buildDateTimeJson("$lt", dateTime);
    }

    private JsonObject buildLiveJson(JsonObject where) {
        final JsonObject liveJson = new JsonObject();
        liveJson.addProperty("$exists", false);
        where.add(Constants.Http.PARAM_DELETED_AT ,liveJson);
        return where;
    }

    private JsonObject buildRequestJson(String method,
                                        String path,
                                        JsonObject body) {
        final JsonObject requestJson = new JsonObject();
        requestJson.addProperty("method", method);
        requestJson.addProperty("path", path);
        requestJson.add("body", body);
        return requestJson;
    }

    public boolean getMobileRegistered(String mobile) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty(Constants.Http.PARAM_USERNAME, mobile);
        String where = whereJson.toString();
        JsonObject result = getUserService().getCount(where, 1, 0);
        return result.get("count").getAsInt() > 0;
    }

    public boolean getScreenNameRegistered(String screenName) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty(Constants.Http.User.PARAM_SCREEN_NAME, screenName);
        String where = whereJson.toString();
        JsonObject result = getUserService().getCount(where, 1, 0);
        return result.get("count").getAsInt() > 0;
    }

    public boolean getEmailRegistered(String email) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty(Constants.Http.User.PARAM_EMAIL, email);
        String where = whereJson.toString();
        JsonObject result = getUserService().getCount(where, 1, 0);
        return result.get("count").getAsInt() > 0;
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

    public User getProfileById(String id) {
        return getUserService().getById(id);
    }

    private String getUserFields() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                Constants.Http.User.PARAM_SCREEN_NAME,
                Constants.Http.User.PARAM_AVATAR_URL,
                Constants.Http.User.PARAM_COUNTRY,
                Constants.Http.User.PARAM_CITY,
                Constants.Http.User.PARAM_AREA,
                Constants.Http.User.PARAM_ABOUT,
                Constants.Http.User.PARAM_MOMENTS,
                Constants.Http.User.PARAM_COVER_URL);
    }

    public User getUserById(String id) {
        return getUserService().getById(id, getUserFields());
    }

    private String getUserInfoFields() {
        return String.format("%s,%s,%s,%s,%s",
                Constants.Http.User.PARAM_CHAT_ID,
                Constants.Http.User.PARAM_SCREEN_NAME,
                Constants.Http.User.PARAM_AVATAR_URL,
                Constants.Http.User.PARAM_CITY,
                Constants.Http.User.PARAM_CREDIT);
    }

    public UserInfo getUserInfoById(String id) {
        return getUserService().getInfoById(id, getUserInfoFields());
    }

    public User getUserByScreenName(String screenName) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty(Constants.Http.User.PARAM_SCREEN_NAME, screenName);
        String where = whereJson.toString();
        List<User> result = getUserService()
                .getList(getUserFields(), where).getResults();
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public UserInfo getUserInfoByChatId(String id) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty(Constants.Http.User.PARAM_CHAT_ID, id);
        String where = whereJson.toString();
        List<UserInfo> result = getUserService()
                .getInfoList(getUserInfoFields(), where)
                .getResults();
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public List<UserInfo> getUserInfoList(List<String> idList) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        String where = whereJson.toString();
        return getUserService()
                .getInfoList(getUserInfoFields(), where)
                .getResults();
    }

    public List<UserInfo> getGroupUsers(List<String> chatIds) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add(Constants.Http.User.PARAM_CHAT_ID, buildIdListJson(chatIds));
        String where = whereJson.toString();
        return getUserService()
                .getInfoList(getUserInfoFields(), where)
                .getResults();
    }

    public String getUserByName(String name) {
        final JsonObject whereJson = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        JsonObject screenNameJson = new JsonObject();
        screenNameJson.addProperty(Constants.Http.User.PARAM_SCREEN_NAME, name);
        jsonArray.add(screenNameJson);
        JsonObject userNameJson = new JsonObject();
        userNameJson.addProperty(Constants.Http.PARAM_USERNAME, name);
        jsonArray.add(userNameJson);
        whereJson.add("$or", jsonArray);
        String where = whereJson.toString();
        List<User> results = getUserService()
                .getList(Constants.Http.PARAM_OBJECT_ID, where).getResults();
        if (results.size() > 0) {
            return results.get(0).getObjectId();
        }
        return null;
    }

    private JsonObject buildAreaUsersJson(String userId, String area) {
        final JsonObject areaUsersJson = new JsonObject();
        final JsonObject userIdJson = new JsonObject();
        userIdJson.addProperty("$ne", userId);
        areaUsersJson.add(Constants.Http.PARAM_OBJECT_ID, userIdJson);
        areaUsersJson.addProperty(Constants.Http.User.PARAM_AREA, area);
        return areaUsersJson;
    }

    public List<UserInfo> getAreaUsers(String userId, String area) {
        final JsonObject whereJson = buildAreaUsersJson(userId, area);
        String where = whereJson.toString();
        return getUserService()
                .getInfoList(getUserInfoFields(), where)
                .getResults();
    }

    public int getAreaUsersCount(String userId, String area) {
        final JsonObject whereJson = buildAreaUsersJson(userId, area);
        String where = whereJson.toString();
        JsonObject result = getUserService().getCount(where, 1, 0);
        return result.get("count").getAsInt();
    }

    public List<UserInfo> getCityUsers(String userId, String city) {
        final JsonObject whereJson = new JsonObject();
        final JsonObject userIdJson = new JsonObject();
        userIdJson.addProperty("$ne", userId);
        whereJson.add(Constants.Http.PARAM_OBJECT_ID, userIdJson);
        whereJson.addProperty(Constants.Http.User.PARAM_CITY, city);
        String where = whereJson.toString();
        return getUserService()
                .getInfoList(getUserInfoFields(), where)
                .getResults();
    }

    public List<UserInfo> getCreditUsers(int limit) {
        return getUserService()
                .getInfoList(getUserInfoFields(), "-credit", limit)
                .getResults();
    }

    public JsonObject updateUserAvatar(String userId, String avatarUrl) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.User.PARAM_AVATAR_URL, avatarUrl);
        return getUserService().updateById(userId, data);
    }

    public JsonObject updateUserCover(String userId, String coverUrl) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.User.PARAM_COVER_URL, coverUrl);
        return getUserService().updateById(userId, data);
    }

    public JsonObject updateUserProfile(User user) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.User.PARAM_SCREEN_NAME, user.getScreenName());
        data.addProperty(Constants.Http.User.PARAM_EMAIL, user.getEmail());
        data.addProperty(Constants.Http.User.PARAM_COUNTRY, user.getCountry());
        data.addProperty(Constants.Http.User.PARAM_CITY, user.getCity());
        data.addProperty(Constants.Http.User.PARAM_AREA, user.getArea());
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

    public JsonObject updateUserAbout(String userId, String about) {
        final JsonObject data = new JsonObject();
        data.addProperty(Constants.Http.User.PARAM_ABOUT, about);
        return getUserService().updateById(userId, data);
    }

    private JsonObject buildUserMomentsRequestJson(String op,
                                                   String userId,
                                                   String momentId) {
        final JsonObject body = new JsonObject();
        final JsonArray userMoments = new JsonArray();
        userMoments.add(new JsonPrimitive(momentId));
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", op);
        opJson.add("objects", userMoments);
        body.add(Constants.Http.User.PARAM_MOMENTS, opJson);
        String path = Constants.Http.URL_USERS_FRAG + "/" + userId;
        return buildRequestJson("PUT", path, body);
    }

    private JsonObject buildUserCreditRequestJson(String userId) {
        final JsonObject body = new JsonObject();
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", "Increment");
        opJson.addProperty("amount", 100);
        body.add(Constants.Http.User.PARAM_CREDIT, opJson);
        String path = Constants.Http.URL_USERS_FRAG + "/" + userId;
        return buildRequestJson("PUT", path, body);
    }

    public JsonArray addUserMoment(String userId, String momentId) {
        final JsonObject script = new JsonObject();
        final JsonArray requests = new JsonArray();
        requests.add(buildUserMomentsRequestJson("AddUnique", userId, momentId));
        requests.add(buildUserCreditRequestJson(userId));
        script.add(Constants.Http.Batch.PARAM_REQUESTS, requests);
        return getBatchService().execute(script);
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
        whereJson.addProperty(Constants.Http.Area.PARAM_CITY, cityId);
        String where = whereJson.toString();
        return getAreaService().getList(where, Integer.MAX_VALUE).getResults();
    }

    public Moment newMoment(Moment moment) {
        Gson gson = new Gson();
        JsonObject data = gson.toJsonTree(moment).getAsJsonObject();
        return getMomentService().newMoment(data);
    }

    public Moment getMomentById(String momentId) {
        return getMomentService().getById(momentId);
    }

    public List<Moment> getMomentsAfter(String after, int limit) {
        final JsonObject whereJson = new JsonObject();
        if (after != null) {
            whereJson.add("createdAt", buildDateTimeAfterJson(after));
        }
        String where = buildLiveJson(whereJson).toString();
        return getMomentService().getList("-createdAt", where, limit).getResults();
    }

    private List<Moment> getMomentsBeforeCore(JsonObject whereJson,
                                              String before,
                                              int limit) {
        if (before != null) {
            whereJson.add("createdAt", buildDateTimeBeforeJson(before));
        }
        String where = buildLiveJson(whereJson).toString();
        return getMomentService().getList("-createdAt", where, limit).getResults();
    }

    public List<Moment> getMomentsBefore(String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        return getMomentsBeforeCore(whereJson, before, limit);
    }

    public List<Moment> getMomentsBefore(List<String> idList,
                                         String before,
                                         int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("objectId", buildIdListJson(idList));
        return getMomentsBeforeCore(whereJson, before, limit);
    }

    public List<Moment> getHotMoments(int limit) {
        return getMomentService().getList("-hot", null, limit).getResults();
    }

    private JsonObject buildMomentLikesRequestJson(String op,
                                                   String momentId,
                                                   String userId) {
        final JsonObject body = new JsonObject();
        final JsonArray momentLikes = new JsonArray();
        momentLikes.add(new JsonPrimitive(userId));
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", op);
        opJson.add("objects", momentLikes);
        body.add(Constants.Http.Moment.PARAM_LIKES, opJson);
        String path = Constants.Http.URL_MOMENTS_FRAG + "/" + momentId;
        return buildRequestJson("PUT", path, body);
    }

    private JsonObject buildMomentHotRequestJson(String momentId, int credit) {
        final JsonObject body = new JsonObject();
        final JsonObject opJson = new JsonObject();
        opJson.addProperty("__op", "Increment");
        opJson.addProperty("amount", credit);
        body.add(Constants.Http.Moment.PARAM_HOT, opJson);
        String path = Constants.Http.URL_MOMENTS_FRAG + "/" + momentId;
        return buildRequestJson("PUT", path, body);
    }

    public JsonArray addMomentLike(String momentId, String userId) {
        final JsonObject script = new JsonObject();
        final JsonArray requests = new JsonArray();
        requests.add(buildMomentLikesRequestJson("AddUnique", momentId, userId));
        requests.add(buildMomentHotRequestJson(momentId, 5));
        script.add(Constants.Http.Batch.PARAM_REQUESTS, requests);
        return getBatchService().execute(script);
    }

    public JsonArray removeMomentLike(String momentId, String userId) {
        final JsonObject script = new JsonObject();
        final JsonArray requests = new JsonArray();
        requests.add(buildMomentLikesRequestJson("Remove", momentId, userId));
        requests.add(buildMomentHotRequestJson(momentId, -5));
        script.add(Constants.Http.Batch.PARAM_REQUESTS, requests);
        return getBatchService().execute(script);
    }

    public List<Comment> getMomentComments(String momentId) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty(Constants.Http.MomentComment.PARAM_PARENT_ID, momentId);
        String where = buildLiveJson(whereJson).toString();
        return getMomentCommentService()
                .getList("-createdAt", where, Integer.MAX_VALUE)
                .getResults();
    }

    private JsonObject buildNewCommentRequestJson(Comment comment) {
        Gson gson = new Gson();
        JsonObject body = gson.toJsonTree(comment).getAsJsonObject();
        String path = Constants.Http.URL_MOMENT_COMMENTS_FRAG;
        return buildRequestJson("POST", path, body);
    }

    public Comment newMomentComment(Comment comment) {
        final JsonObject script = new JsonObject();
        final JsonArray requests = new JsonArray();
        requests.add(buildNewCommentRequestJson(comment));
        requests.add(buildMomentHotRequestJson(comment.getParentId(), 10));
        script.add(Constants.Http.Batch.PARAM_REQUESTS, requests);
        JsonArray result = getBatchService().execute(script);
        JsonElement data = result.get(0).getAsJsonObject().get("success");
        Gson gson = new Gson();
        return gson.fromJson(data, new TypeToken<Comment>(){}.getType());
    }

    private JsonObject buildDeleteCommentRequestJson(String commentId) {
        final JsonObject body = new JsonObject();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        body.addProperty(Constants.Http.MomentComment.PARAM_DELETED_AT,
                now.toString(Constants.DateTime.FORMAT));
        String path = Constants.Http.URL_MOMENT_COMMENTS_FRAG + "/" + commentId;
        return buildRequestJson("PUT", path, body);
    }

    public JsonArray deleteMomentComment(Comment comment) {
        final JsonObject script = new JsonObject();
        final JsonArray requests = new JsonArray();
        requests.add(buildDeleteCommentRequestJson(comment.getObjectId()));
        requests.add(buildMomentHotRequestJson(comment.getParentId(), -10));
        script.add(Constants.Http.Batch.PARAM_REQUESTS, requests);
        return getBatchService().execute(script);
    }
}