package com.aumum.app.mobile.ui.contact;

import android.content.Context;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MessageStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.utils.NotificationUtils;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.NewMessageEvent;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.easemob.chat.EMContactListener;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 19/11/2014.
 */
public class ContactListener implements EMContactListener {

    @Inject UserStore userStore;
    @Inject MessageStore messageStore;
    @Inject RestService restService;
    @Inject ChatService chatService;

    private Context context;
    private Bus bus;

    public ContactListener(Context context, Bus bus) {
        Injector.inject(this);
        this.context = context;
        this.bus = bus;
    }

    @Override
    public void onContactAdded(final List<String> contacts) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                for (String contactId : contacts) {
                    UserInfo user = userStore.getUserInfoByChatId(contactId);
                    restService.addContact(currentUser.getObjectId(), user.getObjectId());
                    currentUser.addContact(user.getObjectId());
                    userStore.save(currentUser);
                }
                return true;
            }
        }.execute();
    }

    @Override
    public void onContactDeleted(final List<String> contacts) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                for (String contactId: contacts) {
                    UserInfo user = userStore.getUserInfoByChatId(contactId);
                    chatService.deleteConversation(contactId);
                    restService.removeContact(currentUser.getObjectId(), user.getObjectId());
                    currentUser.removeContact(user.getObjectId());
                    messageStore.deleteContactRequest(user.getObjectId());
                    userStore.save(currentUser);
                }
                return true;
            }
        }.execute();
    }

    @Override
    public void onContactInvited(final String userName, final String reason) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                UserInfo user = userStore.getUserInfoByChatId(userName);
                if (!currentUser.isContact(user.getObjectId()) &&
                    !messageStore.hasContactRequest(user.getObjectId())) {
                    messageStore.addContactRequest(user.getObjectId(), reason);
                    bus.post(new NewMessageEvent());
                    pushContactInvitedNotification(
                            user.getScreenName(), reason, user.getAvatarUrl());
                }
                return true;
            }
        }.execute();
    }

    @Override
    public void onContactAgreed(final String userName) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                UserInfo user = userStore.getUserInfoByChatId(userName);
                pushContactAgreedNotification(
                        user.getScreenName(), user.getAvatarUrl());
                return true;
            }
        }.execute();
    }

    @Override
    public void onContactRefused(String userName) {
        return;
    }

    private void pushContactInvitedNotification(String userName,
                                                String reason,
                                                String avatarUrl) {
        String content = context.getString(R.string.label_contact_invited, userName);
        NotificationUtils.notify(
                context, content, reason, avatarUrl, ContactRequestsActivity.class);
    }

    private void pushContactAgreedNotification(String userName,
                                               String avatarUrl) {
        String content = context.getString(R.string.label_contact_agreed);
        NotificationUtils.notify(
                context, userName, content, avatarUrl, ChatActivity.class);
    }
}
