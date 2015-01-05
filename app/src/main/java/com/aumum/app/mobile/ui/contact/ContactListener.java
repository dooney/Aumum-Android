package com.aumum.app.mobile.ui.contact;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.NotificationService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.Ln;
import com.easemob.chat.EMContactListener;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 19/11/2014.
 */
public class ContactListener implements EMContactListener {

    @Inject UserStore userStore;
    @Inject RestService restService;
    @Inject NotificationService notificationService;
    @Inject ChatService chatService;
    @Inject ApiKeyProvider apiKeyProvider;

    public ContactListener() {
        Injector.inject(this);
    }

    @Override
    public void onContactAdded(List<String> contacts) {
        try {
            String currentUserId = apiKeyProvider.getAuthUserId();
            for (String contactId : contacts) {
                User user = userStore.getUserByChatId(contactId);
                userStore.addContact(currentUserId, user.getObjectId());
                restService.addContact(currentUserId, user.getObjectId());
            }
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    @Override
    public void onContactDeleted(List<String> contacts) {
        try {
            String currentUserId = apiKeyProvider.getAuthUserId();
            for (String contactId: contacts) {
                User user = userStore.getUserByChatId(contactId);
                chatService.deleteConversation(contactId);
                userStore.removeContact(currentUserId, user.getObjectId());
                restService.removeContact(currentUserId, user.getObjectId());
            }
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    @Override
    public void onContactInvited(String userName, String reason) {
        try {
            User currentUser = userStore.getCurrentUser();
            User user = userStore.getUserByChatId(userName);
            if (!currentUser.getContacts().contains(user.getObjectId())) {
                userStore.addContactRequest(user.getObjectId(), reason);
                notificationService.pushContactInvitedNotification(user.getScreenName(), reason);
            }
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    @Override
    public void onContactAgreed(String userName) {
        try {
            User user = userStore.getUserByChatId(userName);
            notificationService.pushContactAgreedNotification(user.getChatId(), user.getScreenName());
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    @Override
    public void onContactRefused(String userName) {
        return;
    }
}
