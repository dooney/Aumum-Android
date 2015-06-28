package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.UserEntity;
import com.aumum.app.mobile.core.dao.entity.UserInfoEntity;
import com.aumum.app.mobile.core.dao.gen.UserEntityDao;
import com.aumum.app.mobile.core.dao.gen.UserInfoEntityDao;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 6/10/2014.
 */
public class UserStore {

    private RestService restService;
    private ApiKeyProvider apiKeyProvider;
    private UserEntityDao userEntityDao;
    private UserInfoEntityDao userInfoEntityDao;

    public static final int LIMIT_TOP = 10;

    public UserStore(RestService restService, ApiKeyProvider apiKeyProvider, Repository repository) {
        this.restService = restService;
        this.apiKeyProvider = apiKeyProvider;
        this.userEntityDao = repository.getUserEntityDao();
        this.userInfoEntityDao = repository.getUserInfoEntityDao();
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
            return gson.fromJson(data, new TypeToken<List<String>>() {
            }.getType());
        }
        return null;
    }

    private User map(UserEntity userEntity) {
        String createdAt = DateUtils.dateToString(userEntity.getCreatedAt(), Constants.DateTime.FORMAT);
        return new User(
                userEntity.getObjectId(),
                userEntity.getUsername(),
                userEntity.getChatId(),
                createdAt,
                userEntity.getScreenName(),
                userEntity.getEmail(),
                userEntity.getCountry(),
                userEntity.getCity(),
                userEntity.getArea(),
                userEntity.getAvatarUrl(),
                userEntity.getAbout(),
                getList(userEntity.getContacts()));
    }

    private UserEntity map(User user) throws Exception {
        Date createdAt = DateUtils.stringToDate(user.getCreatedAt(), Constants.DateTime.FORMAT);
        return new UserEntity(
                user.getObjectId(),
                user.getUsername(),
                user.getChatId(),
                createdAt,
                user.getScreenName(),
                user.getEmail(),
                user.getCountry(),
                user.getCity(),
                user.getArea(),
                user.getAvatarUrl(),
                user.getAbout(),
                getJsonString(user.getContacts()));
    }

    private UserInfo map(UserInfoEntity userInfoEntity) {
        String createdAt = DateUtils.dateToString(userInfoEntity.getCreatedAt(), Constants.DateTime.FORMAT);
        return new UserInfo(
                userInfoEntity.getObjectId(),
                userInfoEntity.getChatId(),
                createdAt,
                userInfoEntity.getScreenName(),
                userInfoEntity.getAvatarUrl());
    }

    private UserInfoEntity map(UserInfo userInfo) throws Exception {
        Date createdAt = DateUtils.stringToDate(userInfo.getCreatedAt(), Constants.DateTime.FORMAT);
        return new UserInfoEntity(
                userInfo.getObjectId(),
                userInfo.getChatId(),
                createdAt,
                userInfo.getScreenName(),
                userInfo.getAvatarUrl(),
                userInfo.getCity(),
                userInfo.getCredit());
    }

    private void updateOrInsert(List<UserInfo> users) throws Exception {
        for (UserInfo user: users) {
            userInfoEntityDao.insertOrReplace(map(user));
        }
    }

    public User getCurrentUser() throws Exception {
        String id = apiKeyProvider.getAuthUserId();
        UserEntity userEntity = userEntityDao.load(id);
        if (userEntity != null) {
            return map(userEntity);
        } else {
            User user = restService.getProfileById(id);
            userEntityDao.insertOrReplace(map(user));
            return user;
        }
    }

    public User getUserByIdFromServer(String id) throws Exception {
        User user = restService.getUserById(id);
        userEntityDao.insertOrReplace(map(user));
        return user;
    }

    public User getUserByScreenNameFromServer(String screenName) throws Exception {
        User user = restService.getUserByScreenName(screenName);
        if (user != null) {
            userEntityDao.insertOrReplace(map(user));
            return user;
        }
        return null;
    }

    public UserInfo getUserInfoByChatIdFromServer(String id) throws Exception {
        UserInfo userInfo = restService.getUserInfoByChatId(id);
        if (userInfo != null) {
            userInfoEntityDao.insertOrReplace(map(userInfo));
            return userInfo;
        }
        return null;
    }

    public UserInfo getUserInfoById(String id) throws Exception {
        UserInfoEntity userInfoEntity = userInfoEntityDao.load(id);
        if (userInfoEntity != null) {
            return map(userInfoEntity);
        } else {
            UserInfo userInfo = restService.getUserInfoById(id);
            userInfoEntityDao.insertOrReplace(map(userInfo));
            return userInfo;
        }
    }

    public UserInfo getUserInfoByChatId(String id) throws Exception {
        UserInfoEntity userInfoEntity = userInfoEntityDao.queryBuilder()
                .where(UserInfoEntityDao.Properties.ChatId.eq(id))
                .unique();
        if (userInfoEntity != null) {
            return map(userInfoEntity);
        } else {
            return getUserInfoByChatIdFromServer(id);
        }
    }

    public List<UserInfo> getUserInfoList(List<String> idList) throws Exception {
        List<UserInfo> localList = new ArrayList<>();
        List<String> requestList = new ArrayList<>();
        for (String id: idList) {
            UserInfoEntity userInfoEntity = userInfoEntityDao.load(id);
            if (userInfoEntity != null) {
                localList.add(map(userInfoEntity));
            } else {
                requestList.add(id);
            }
        }
        if (requestList.size() > 0) {
            List<UserInfo> fetchList = restService.getUserInfoList(requestList);
            updateOrInsert(fetchList);
            localList.addAll(fetchList);
            List<UserInfo> result = new ArrayList<>();
            for (String id : idList) {
                for (UserInfo user : localList) {
                    if (user.getObjectId().equals(id)) {
                        result.add(user);
                    }
                }
            }
            return result;
        }
        return localList;
    }

    public List<UserInfo> getListByCity(String userId, String city) throws Exception {
        List<UserInfo> users = restService.getCityUsers(userId, city, LIMIT_TOP);
        updateOrInsert(users);
        return users;
    }

    public List<UserInfo> getLocalNearByList() {
        String userId = apiKeyProvider.getAuthUserId();
        UserEntity entity = userEntityDao.load(userId);
        List<UserInfoEntity> entities = userInfoEntityDao.queryBuilder()
                .where(UserInfoEntityDao.Properties.City.eq(entity.getCity()))
                .where(UserInfoEntityDao.Properties.ObjectId.notEq(userId))
                .list();
        Collections.shuffle(entities);
        List<UserInfo> userInfoList = new ArrayList<>();
        for (int i = 0; i < entities.size() && i <= LIMIT_TOP; i++) {
            UserInfo userInfo = map(entities.get(i));
            userInfoList.add(userInfo);
        }
        return userInfoList;
    }

    public List<UserInfo> getTalentList() throws Exception {
        List<UserInfo> users = restService.getCreditUsers(LIMIT_TOP);
        updateOrInsert(users);
        return users;
    }

    public List<UserInfo> getLocalTalentList() {
        List<UserInfoEntity> entities = userInfoEntityDao.queryBuilder()
                .orderDesc(UserInfoEntityDao.Properties.Credit)
                .limit(LIMIT_TOP)
                .list();
        List<UserInfo> userInfoList = new ArrayList<>();
        for (UserInfoEntity entity: entities) {
            UserInfo userInfo = map(entity);
            userInfoList.add(userInfo);
        }
        return userInfoList;
    }

    public void save(User user) throws Exception {
        userEntityDao.insertOrReplace(map(user));
    }
}