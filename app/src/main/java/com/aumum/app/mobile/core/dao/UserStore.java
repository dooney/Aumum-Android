package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.UserEntity;
import com.aumum.app.mobile.core.dao.gen.UserEntityDao;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 6/10/2014.
 */
public class UserStore {

    private RestService restService;
    private ApiKeyProvider apiKeyProvider;
    private UserEntityDao userEntityDao;

    public UserStore(RestService restService, ApiKeyProvider apiKeyProvider, Repository repository) {
        this.restService = restService;
        this.apiKeyProvider = apiKeyProvider;
        this.userEntityDao = repository.getUserEntityDao();
    }

    private String getJsonString(List<String> list) {
        if (list != null) {
            Gson gson = new Gson();
            return gson.toJson(list);
        }
        return null;
    }

    private List<String> getList(String data) {
        if (data != null) {
            Gson gson = new Gson();
            return gson.fromJson(data, new TypeToken<List<String>>(){}.getType());
        }
        return null;
    }

    private User map(UserEntity userEntity) {
        String createdAt = DateUtils.dateToString(userEntity.getCreatedAt(), Constants.DateTime.FORMAT);
        return new User(
                userEntity.getObjectId(),
                createdAt,
                userEntity.getScreenName(),
                userEntity.getArea(),
                userEntity.getAvatarUrl(),
                userEntity.getAbout(),
                getList(userEntity.getContacts()),
                getList(userEntity.getComments()),
                getList(userEntity.getMessages()),
                getList(userEntity.getParties()),
                getList(userEntity.getPartyPosts()),
                getList(userEntity.getMoments()),
                getList(userEntity.getMomentPosts()));
    }

    private UserEntity map(User user) throws Exception {
        Date createdAt = DateUtils.stringToDate(user.getCreatedAt(), Constants.DateTime.FORMAT);
        UserEntity userEntity = new UserEntity(
                user.getObjectId(),
                createdAt,
                user.getScreenName(),
                user.getArea(),
                user.getAvatarUrl(),
                user.getAbout(),
                getJsonString(user.getContacts()),
                getJsonString(user.getComments()),
                getJsonString(user.getMessages()),
                getJsonString(user.getParties()),
                getJsonString(user.getPartyPosts()),
                getJsonString(user.getMoments()),
                getJsonString(user.getMomentPosts()));
        return userEntity;
    }

    public User getCurrentUserFromServer() throws Exception {
        String currentUserId = apiKeyProvider.getAuthUserId();
        return getUserByIdFromServer(currentUserId);
    }

    public User getCurrentUser() throws Exception {
        String currentUserId = apiKeyProvider.getAuthUserId();
        return getUserById(currentUserId);
    }

    public User getUserByIdFromServer(String id) throws Exception {
        User user = restService.getUserById(id);
        userEntityDao.insertOrReplace(map(user));
        return user;
    }

    public User getUserById(String id) throws Exception {
        UserEntity userEntity = userEntityDao.load(id);
        if (userEntity != null) {
            return map(userEntity);
        } else {
            return getUserByIdFromServer(id);
        }
    }
}