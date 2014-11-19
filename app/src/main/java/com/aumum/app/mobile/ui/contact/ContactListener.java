package com.aumum.app.mobile.ui.contact;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.service.NotificationService;
import com.easemob.chat.EMContactListener;
import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 19/11/2014.
 */
public class ContactListener implements EMContactListener {

    @Inject UserStore userStore;
    @Inject NotificationService notificationService;

    public ContactListener() {
        Injector.inject(this);
    }

    @Override
    public void onContactAdded(List<String> strings) {

    }

    @Override
    public void onContactDeleted(List<String> strings) {

    }

    @Override
    public void onContactInvited(String userName, String reason) {
        Gson gson = new Gson();
        AddContactRequest request = gson.fromJson(reason, AddContactRequest.class);
        userStore.addContactRequest(request.getUserId(), request.getIntro());
        notificationService.pushContactInvitedNotification(request.getUserName(), request.getIntro());
    }

    @Override
    public void onContactAgreed(String s) {

    }

    @Override
    public void onContactRefused(String s) {

    }
}
