package com.aumum.app.mobile.ui.contact;

import android.app.Activity;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.CreditRuleStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CreditRule;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.NotificationService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.easemob.chat.EMContactListener;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 19/11/2014.
 */
public class ContactListener implements EMContactListener {

    @Inject UserStore userStore;
    @Inject CreditRuleStore creditRuleStore;
    @Inject RestService restService;
    @Inject NotificationService notificationService;
    @Inject ChatService chatService;

    private Activity activity;

    public ContactListener(Activity activity) {
        Injector.inject(this);
        this.activity = activity;
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
                    updateCredit(currentUser, CreditRule.ADD_CONTACT);
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
                    updateCredit(currentUser, CreditRule.DELETE_CONTACT);
                    currentUser.removeContact(user.getObjectId());
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
                if (!currentUser.isContact(user.getObjectId())) {
                    userStore.addContactRequest(user.getObjectId(), reason);
                    notificationService.pushContactInvitedNotification(user.getScreenName(), reason);
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
                notificationService.pushContactAgreedNotification(user.getChatId(),
                        user.getScreenName());
                return true;
            }
        }.execute();
    }

    @Override
    public void onContactRefused(String userName) {
        return;
    }

    private void updateCredit(User currentUser, int seq) throws Exception {
        final CreditRule creditRule = creditRuleStore.getCreditRuleBySeq(seq);
        if (creditRule != null) {
            final int credit = creditRule.getCredit();
            restService.updateUserCredit(currentUser.getObjectId(), credit);
            currentUser.updateCredit(credit);
            userStore.save(currentUser);
            if (credit > 0) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toaster.showShort(activity, activity.getString(R.string.info_got_credit,
                                creditRule.getDescription(), credit));
                    }
                });
            }
        }
    }
}
