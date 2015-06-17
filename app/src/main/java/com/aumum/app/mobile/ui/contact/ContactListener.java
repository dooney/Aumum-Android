package com.aumum.app.mobile.ui.contact;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MessageStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.utils.EMChatUtils;
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
                    EMChatUtils.deleteConversation(contactId);
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
                        user.getChatId(), user.getScreenName(), user.getAvatarUrl());
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
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context, ContactRequestsActivity.class));
        NotificationUtils.notify(context, content, reason, avatarUrl, intent);
    }

    private void pushContactAgreedNotification(String chatId,
                                               String userName,
                                               String avatarUrl) {
        String content = context.getString(R.string.label_contact_agreed);
        Intent intent = new Intent();
        intent.putExtra(ChatActivity.INTENT_ID, chatId);
        intent.putExtra(ChatActivity.INTENT_TITLE, userName);
        intent.setComponent(new ComponentName(context, ChatActivity.class));
        NotificationUtils.notify(context, userName, content, avatarUrl, intent);
    }
}
