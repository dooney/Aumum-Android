package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.entity.ContactRequestEntity;
import com.aumum.app.mobile.core.dao.entity.UserEntity;
import com.aumum.app.mobile.core.dao.gen.ContactRequestEntityDao;
import com.aumum.app.mobile.core.dao.gen.UserEntityDao;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.contact.ContactRequest;
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
    private ContactRequestEntityDao contactRequestEntityDao;

    public UserStore(RestService restService, ApiKeyProvider apiKeyProvider, Repository repository) {
        this.restService = restService;
        this.apiKeyProvider = apiKeyProvider;
        this.userEntityDao = repository.getUserEntityDao();
        this.contactRequestEntityDao = repository.getContactRequestEntityDao();
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
                userEntity.getChatId(),
                createdAt,
                userEntity.getScreenName(),
                userEntity.getArea(),
                userEntity.getAvatarUrl(),
                userEntity.getAbout(),
                getList(userEntity.getContacts()),
                getList(userEntity.getMessages()),
                getList(userEntity.getParties()),
                getList(userEntity.getAskings()));
    }

    private UserEntity map(User user) throws Exception {
        Date createdAt = DateUtils.stringToDate(user.getCreatedAt(), Constants.DateTime.FORMAT);
        UserEntity userEntity = new UserEntity(
                user.getObjectId(),
                user.getChatId(),
                createdAt,
                user.getScreenName(),
                user.getArea(),
                user.getAvatarUrl(),
                user.getAbout(),
                getJsonString(user.getContacts()),
                getJsonString(user.getMessages()),
                getJsonString(user.getParties()),
                getJsonString(user.getAskings()));
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

    public User getUserByChatIdFromServer(String id) throws Exception {
        User user = restService.getUserByChatId(id);
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

    public User getUserByChatId(String id) throws Exception {
        UserEntity userEntity = userEntityDao.queryBuilder()
                .where(UserEntityDao.Properties.ChatId.eq(id))
                .unique();
        if (userEntity != null) {
            return map(userEntity);
        } else {
            return getUserByChatIdFromServer(id);
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
            User user = getUserById(entity.getUserId());
            boolean isAdded = currentUser.getContacts().contains(entity.getUserId());
            result.add(new ContactRequest(user, entity.getIntro(), isAdded));
        }
        return result;
    }

    public List<User> getContacts() throws Exception {
        User user = getCurrentUser();
        List<User> result = new ArrayList<User>();
        if (user != null) {
            for (String contactId: user.getContacts()) {
                result.add(getUserById(contactId));
            }
        }
        return result;
    }

    public void addContact(String userId) throws Exception {
        User user = getCurrentUser();
        user.getContacts().add(userId);
        update(user);
    }

    public void removeContact(String userId) throws Exception {
        User user = getCurrentUser();
        user.getContacts().remove(userId);
        update(user);
    }

    public void update(User user) throws Exception {
        userEntityDao.insertOrReplace(map(user));
    }
}