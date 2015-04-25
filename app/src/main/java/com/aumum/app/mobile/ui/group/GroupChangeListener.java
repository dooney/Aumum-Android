package com.aumum.app.mobile.ui.group;

import android.app.Activity;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.UserInfo;
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

    @Inject UserStore userStore;
    @Inject ChatService chatService;
    @Inject NotificationService notificationService;

    private Activity activity;
    private Bus bus;

    public GroupChangeListener(Activity activity, Bus bus) {
        this.activity = activity;
        this.bus = bus;
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
                UserInfo user = userStore.getUserInfoByChatId(appliedBy);
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
                UserInfo user = userStore.getUserInfoByChatId(declinedBy);
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
    public void onUserRemoved(final String groupId, final String groupName) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return chatService.deleteGroupConversation(groupId);
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                bus.post(new GroupDeletedEvent());
                Toaster.showShort(activity,
                        activity.getString(R.string.info_user_was_removed, groupName));
            }
        }.execute();
    }

    @Override
    public void onGroupDestroy(final String groupId, final String groupName) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return chatService.deleteGroupConversation(groupId);
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                bus.post(new GroupDeletedEvent());
                Toaster.showShort(activity,
                        activity.getString(R.string.info_group_name_deleted, groupName));
            }
        }.execute();
    }
}
