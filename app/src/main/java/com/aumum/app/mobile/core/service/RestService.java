
package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.model.Area;
import com.aumum.app.mobile.core.model.CityGroup;
import com.aumum.app.mobile.core.model.CreditRule;
import com.aumum.app.mobile.core.model.Feedback;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.Report;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.core.model.UserTag;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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

    private CityGroupService getCityGroupService() {
        return getRestAdapter().create(CityGroupService.class);
    }

    private UserTagService getUserTagService() {
        return getRestAdapter().create(UserTagService.class);
    }

    private CreditRuleService getCreditRuleService() {
        return getRestAdapter().create(CreditRuleService.class);
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
        return String.format("%s,%s,%s,%s,%s,%s,%s",
                Constants.Http.User.PARAM_SCREEN_NAME,
                Constants.Http.User.PARAM_AVATAR_URL,
                Constants.Http.User.PARAM_CITY,
                Constants.Http.User.PARAM_AREA,
                Constants.Http.User.PARAM_TAGS,
                Constants.Http.User.PARAM_ABOUT,
                Constants.Http.User.PARAM_CREDIT);
    }

    public User getUserById(String id) {
        return getUserService().getById(id, getUserFields());
    }

    private String getUserInfoFields() {
        return String.format("%s,%s,%s",
                Constants.Http.User.PARAM_CHAT_ID,
                Constants.Http.User.PARAM_SCREEN_NAME,
                Constants.Http.User.PARAM_AVATAR_URL);
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

    private JsonObject buildTagUsersJson(String userId, List<String> tags) {
        final JsonObject tagUsersJson = new JsonObject();
        final JsonObject userIdJson = new JsonObject();
        userIdJson.addProperty("$ne", userId);
        tagUsersJson.add(Constants.Http.PARAM_OBJECT_ID, userIdJson);
        if (tags.size() > 1) {
            final JsonArray tagsJson = new JsonArray();
            for(String tag: tags) {
                final JsonObject tagJson = new JsonObject();
                tagJson.addProperty(Constants.Http.User.PARAM_TAGS, tag);
                tagsJson.add(tagJson);
            }
            tagUsersJson.add("$or", tagsJson);
        } else {
            tagUsersJson.addProperty(Constants.Http.User.PARAM_TAGS, tags.get(0));
        }
        return tagUsersJson;
    }

    public List<UserInfo> getTagUsers(String userId, List<String> tags) {
        final JsonObject whereJson = buildTagUsersJson(userId, tags);
        String where = whereJson.toString();
        return getUserService()
                .getInfoList(getUserInfoFields(), where)
                .getResults();
    }

    public int getTagUsersCount(String userId, List<String> tags) {
        final JsonObject whereJson = buildTagUsersJson(userId, tags);
        String where = whereJson.toString();
        JsonObject result = getUserService().getCount(where, 1, 0);
        return result.get("count").getAsInt();
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

    public JsonObject addUserMoment(String userId, String momentId) {
        final JsonObject op = new JsonObject();
        op.addProperty("__op", "AddUnique");
        return updateUserMoments(op, userId, momentId);
    }

    private JsonObject updateUserMoments(JsonObject op, String userId, String momentId) {
        final JsonObject data = new JsonObject();
        final JsonArray userMoments = new JsonArray();
        userMoments.add(new JsonPrimitive(momentId));
        op.add("objects", userMoments);
        data.add(Constants.Http.User.PARAM_MOMENTS, op);
        return getUserService().updateById(userId, data);
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

    public CityGroup getCityGroup(int cityId) {
        final JsonObject whereJson = new JsonObject();
        whereJson.addProperty(Constants.Http.CityGroup.PARAM_CITY, cityId);
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

    public Moment newMoment(Moment moment) {
        Gson gson = new Gson();
        JsonObject data = gson.toJsonTree(moment).getAsJsonObject();
        return getMomentService().newMoment(data);
    }

    public List<Moment> getMomentsAfter(String after, int limit) {
        final JsonObject whereJson = new JsonObject();
        if (after != null) {
            whereJson.add("createdAt", buildDateTimeAfterJson(after));
        }
        String where = buildLiveJson(whereJson).toString();
        return getMomentService().getList("-createdAt", where, limit).getResults();
    }

    public List<Moment> getMomentsBefore(String before, int limit) {
        final JsonObject whereJson = new JsonObject();
        whereJson.add("createdAt", buildDateTimeBeforeJson(before));
        String where = buildLiveJson(whereJson).toString();
        return getMomentService().getList("-createdAt", where, limit).getResults();
    }

    public List<CreditRule> getCreditRuleList() {
        final JsonObject whereJson = new JsonObject();
        String where = buildLiveJson(whereJson).toString();
        return getCreditRuleService().getList(Constants.Http.CreditRule.PARAM_SEQ,
                where, Integer.MAX_VALUE).getResults();
    }
}