package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.gen.MessageVMDao;
import com.aumum.app.mobile.core.dao.gen.UserVMDao;
import com.aumum.app.mobile.core.dao.vm.UserVM;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.DateUtils;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 6/10/2014.
 */
public class UserStore {

    private RestService restService;
    private ApiKeyProvider apiKeyProvider;
    private UserVMDao userVMDao;

    public UserStore(RestService restService, ApiKeyProvider apiKeyProvider, Repository repository) {
        this.restService = restService;
        this.apiKeyProvider = apiKeyProvider;
        this.userVMDao = repository.getUserVMDao();
    }

    private String getJsonString(List<String> list) {
        if (list != null) {
            Gson gson = new Gson();
            return gson.toJson(list);
        }
        return null;
    }

    private UserVM map(User user, Long pk) throws Exception {
        Date createdAt = DateUtils.stringToDate(user.getCreatedAt(), Constants.DateTime.FORMAT);
        UserVM userVM = new UserVM(pk, user.getObjectId(), createdAt, user.getScreenName(), user.getArea(), user.getAvatarUrl(), user.getAbout(),
                getJsonString(user.getFollowers()),
                getJsonString(user.getFollowings()),
                getJsonString(user.getComments()),
                getJsonString(user.getMessages()),
                getJsonString(user.getParties()),
                getJsonString(user.getPartyPosts()),
                getJsonString(user.getMoments()),
                getJsonString(user.getMomentPosts()));
        return userVM;
    }

    public UserVM getCurrentUser(boolean refresh) throws Exception {
        String currentUserId = apiKeyProvider.getAuthUserId();
        return getUserById(currentUserId, refresh);
    }

    private UserVM getUserById(String id, Long pk) throws Exception {
        UserVM user = map(restService.getUserById(id), pk);
        userVMDao.insertOrReplace(user);
        return user;
    }

    public UserVM getUserById(String id, boolean refresh) throws Exception {
        UserVM user = userVMDao.queryBuilder()
                .where(MessageVMDao.Properties.ObjectId.eq(id))
                .unique();
        Long pk = user != null ? user.getId() : null;
        if (refresh || pk == null) {
            return getUserById(id, pk);
        }
        return user;
    }
}