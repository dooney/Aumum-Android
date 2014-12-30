package com.aumum.app.mobile.ui.chat;

import android.app.Activity;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.events.GroupDeletedEvent;
import com.github.kevinsawicki.wishlist.Toaster;
import com.squareup.otto.Bus;

/**
 * Created by Administrator on 30/12/2014.
 */
public class GroupChangeListener implements com.easemob.chat.GroupChangeListener {

    private Activity activity;
    private ChatService chatService;
    private Bus bus;

    public GroupChangeListener(Activity activity,
                               ChatService chatService,
                               Bus bus) {
        this.activity = activity;
        this.chatService = chatService;
        this.bus = bus;
    }

    @Override
    public void onInvitationReceived(String s, String s2, String s3, String s4) {

    }

    @Override
    public void onApplicationReceived(String s, String s2, String s3, String s4) {

    }

    @Override
    public void onApplicationAccept(String s, String s2, String s3) {

    }

    @Override
    public void onApplicationDeclined(String s, String s2, String s3, String s4) {

    }

    @Override
    public void onInvitationAccpted(String s, String s2, String s3) {

    }

    @Override
    public void onInvitationDeclined(String s, String s2, String s3) {

    }

    @Override
    public void onUserRemoved(String s, String s2) {

    }

    @Override
    public void onGroupDestroy(String groupId, String groupName) {
        chatService.deleteGroupConversation(groupId);
        bus.post(new GroupDeletedEvent());
        Toaster.showShort(activity, activity.getString(R.string.info_group_name_deleted, groupName));
    }
}
