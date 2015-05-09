package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.ContactRequestEntity;
import com.aumum.app.mobile.core.dao.entity.GroupRequestEntity;
import com.aumum.app.mobile.core.dao.entity.UserEntity;
import com.aumum.app.mobile.core.dao.entity.UserInfoEntity;
import com.aumum.app.mobile.core.dao.gen.ContactRequestEntityDao;
import com.aumum.app.mobile.core.dao.gen.GroupRequestEntityDao;
import com.aumum.app.mobile.core.dao.gen.UserEntityDao;
import com.aumum.app.mobile.core.dao.gen.UserInfoEntityDao;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.GroupRequest;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.model.ContactRequest;
import com.aumum.app.mobile.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
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
    private ContactRequestEntityDao contactRequestEntityDao;
    private GroupRequestEntityDao groupRequestEntityDao;

    public UserStore(RestService restService, ApiKeyProvider apiKeyProvider, Repository repository) {
        this.restService = restService;
        this.apiKeyProvider = apiKeyProvider;
        this.userEntityDao = repository.getUserEntityDao();
        this.userInfoEntityDao = repository.getUserInfoEntityDao();
        this.contactRequestEntityDao = repository.getContactRequestEntityDao();
        this.groupRequestEntityDao = repository.getGroupRequestEntityDao();
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
                userEntity.getCity(),
                userEntity.getArea(),
                userEntity.getAvatarUrl(),
                userEntity.getAbout(),
                getList(userEntity.getContacts()),
                getList(userEntity.getMoments()),
                userEntity.getCoverUrl());
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
                user.getCity(),
                user.getArea(),
                user.getAvatarUrl(),
                user.getAbout(),
                getJsonString(user.getContacts()),
                getJsonString(user.getMoments()),
                user.getCoverUrl());
    }

    private UserInfo map(UserInfoEntity userInfoEntity) throws Exception {
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
                userInfo.getAvatarUrl());
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

    public void addContactRequest(String userId, String intro) {
        ContactRequestEntity contactRequestEntity = contactRequestEntityDao.queryBuilder()
                .where(ContactRequestEntityDao.Properties.UserId.eq(userId))
                .unique();
        if (contactRequestEntity != null) {
            contactRequestEntityDao.delete(contactRequestEntity);
        }
        contactRequestEntity = new ContactRequestEntity(null, userId, intro);
        contactRequestEntityDao.insert(contactRequestEntity);
    }

    public List<ContactRequest> getContactRequestList() throws Exception {
        User currentUser = getCurrentUser();
        List<ContactRequestEntity> entities = contactRequestEntityDao.queryBuilder()
                .orderDesc(ContactRequestEntityDao.Properties.Id)
                .list();
        List<ContactRequest> result = new ArrayList<ContactRequest>();
        for (ContactRequestEntity entity: entities) {
            UserInfo userInfo = getUserInfoById(entity.getUserId());
            boolean isAdded = currentUser.isContact(entity.getUserId());
            result.add(new ContactRequest(userInfo, entity.getIntro(), isAdded));
        }
        return result;
    }

    public boolean hasContactRequest(String userId) {
        List<ContactRequestEntity> entities = contactRequestEntityDao.queryBuilder()
                .orderDesc(ContactRequestEntityDao.Properties.Id)
                .list();
        for (ContactRequestEntity entity: entities) {
            if (entity.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public void addGroupRequest(String groupId, String userId, String reason) {
        GroupRequestEntity groupRequestEntity = groupRequestEntityDao.queryBuilder()
                .where(GroupRequestEntityDao.Properties.GroupId.eq(groupId))
                .where(GroupRequestEntityDao.Properties.UserId.eq(userId))
                .unique();
        if (groupRequestEntity != null) {
            groupRequestEntityDao.delete(groupRequestEntity);
        }
        groupRequestEntity = new GroupRequestEntity(null, groupId, userId, reason,
                GroupRequest.STATUS_NONE);
        groupRequestEntityDao.insert(groupRequestEntity);
    }

    public void deleteGroupRequests(String groupId) {
        List<GroupRequestEntity> entities = groupRequestEntityDao.queryBuilder()
                .where(GroupRequestEntityDao.Properties.GroupId.eq(groupId))
                .list();
        for (GroupRequestEntity entity: entities) {
            groupRequestEntityDao.deleteByKey(entity.getId());
        }
    }

    public List<GroupRequest> getGroupRequestList() throws Exception {
        List<GroupRequestEntity> entities = groupRequestEntityDao.queryBuilder()
                .orderDesc(GroupRequestEntityDao.Properties.Id)
                .list();
        List<GroupRequest> result = new ArrayList<>();
        for (GroupRequestEntity entity: entities) {
            result.add(new GroupRequest(
                    entity.getId(),
                    entity.getGroupId(),
                    entity.getUserId(),
                    entity.getReason(),
                    entity.getStatus()));
        }
        return result;
    }

    public void saveGroupRequest(GroupRequest request) {
        GroupRequestEntity entity = new GroupRequestEntity(
                request.getId(),
                request.getGroupId(),
                request.getUserId(),
                request.getReason(),
                request.getStatus());
        groupRequestEntityDao.insertOrReplace(entity);
    }

    public List<UserInfo> getContacts() throws Exception {
        User user = getCurrentUser();
        List<UserInfo> result = new ArrayList<>();
        if (user != null) {
            for (String contactId: user.getContacts()) {
                result.add(getUserInfoById(contactId));
            }
        }
        return result;
    }

    public List<UserInfo> getListByArea(String userId, String area) throws Exception {
        List<UserInfo> users = restService.getAreaUsers(userId, area);
        if (users != null) {
            for (UserInfo user: users) {
                userInfoEntityDao.insertOrReplace(map(user));
            }
        }
        return users;
    }

    public List<UserInfo> getListByGroup(List<String> chatIds) throws Exception {
        List<UserInfo> users = restService.getGroupUsers(chatIds);
        if (users != null) {
            for (UserInfo user: users) {
                userInfoEntityDao.insertOrReplace(map(user));
            }
        }
        return users;
    }

    public void save(User user) throws Exception {
        userEntityDao.insertOrReplace(map(user));
    }
}