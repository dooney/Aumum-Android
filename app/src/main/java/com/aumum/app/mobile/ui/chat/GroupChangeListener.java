package com.aumum.app.mobile.ui.chat;

import android.app.Activity;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.NotificationService;
import com.aumum.app.mobile.events.GroupDeletedEvent;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by Administrator on 30/12/2014.
 */
public class GroupChangeListener implements com.easemob.chat.GroupChangeListener {

    private Activity activity;
    @Inject UserStore userStore;
    @Inject ChatService chatService;
    @Inject NotificationService notificationService;
    @Inject Bus bus;

    public GroupChangeListener(Activity activity) {
        this.activity = activity;
        Injector.inject(this);
    }

    @Override
    public void onInvitationReceived(final String groupId,
                                     final String groupName,
                                     final String invitedBy,
                                     final String reason) {

    }

    @Override
    public void onApplicationReceived(final String groupId,
                                      final String groupName,
                                      final String appliedBy,
                                      final String reason) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                userStore.addGroupRequest(groupId, appliedBy, reason);
                User user = userStore.getUserByChatId(appliedBy);
                notificationService.pushGroupAppliedNotification(user.getScreenName(), groupName, reason);
                return true;
            }
        }.execute();
    }

    @Override
    public void onApplicationAccept(final String groupId,
                                    final String groupName,
                                    final String acceptedBy) {
        notificationService.pushGroupApprovedNotification(groupId, groupName);
    }

    @Override
    public void onApplicationDeclined(final String groupId,
                                      final String groupName,
                                      final String declinedBy,
                                      final String reason) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                User user = userStore.getUserByChatId(declinedBy);
                String title = activity.getString(R.string.label_application_declined,
                        user.getScreenName());
                notificationService.pushUserDetailsNotification(user.getObjectId(), title, reason);
                return true;
            }
        }.execute();
    }

    @Override
    public void onInvitationAccpted(final String groupId, String groupOwner, String reason) {

    }

    @Override
    public void onInvitationDeclined(String groupId, String invitee, String reason) {

    }

    @Override
    public void onUserRemoved(String groupId, String groupName) {

    }

    @Override
    public void onGroupDestroy(String groupId, String groupName) {
        if (chatService.deleteGroupConversation(groupId)) {
            bus.post(new GroupDeletedEvent());
            Toaster.showShort(activity, activity.getString(R.string.info_group_name_deleted, groupName));
        }
    }
}
