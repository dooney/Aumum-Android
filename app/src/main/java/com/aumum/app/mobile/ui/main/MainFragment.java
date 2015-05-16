package com.aumum.app.mobile.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.events.NewMessageEvent;
import com.aumum.app.mobile.events.NewChatMessageEvent;
import com.aumum.app.mobile.events.ResetChatUnreadEvent;
import com.aumum.app.mobile.events.ResetMessageUnreadEvent;
import com.aumum.app.mobile.ui.chat.ChatConnectionListener;
import com.aumum.app.mobile.ui.group.GroupChangeListener;
import com.aumum.app.mobile.ui.chat.MessageNotifyListener;
import com.aumum.app.mobile.ui.chat.NotificationClickListener;
import com.aumum.app.mobile.ui.contact.ContactListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.Ln;
import com.easemob.chat.EMMessage;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Fragment which houses the View pager.
 */
public class MainFragment extends Fragment {

    @Inject ChatService chatService;
    @Inject FileUploadService fileUploadService;
    @Inject ApiKeyProvider apiKeyProvider;
    @Inject Bus bus;

    private ConnectionChangeReceiver connectionChangeReceiver;
    private NewMessageBroadcastReceiver newMessageBroadcastReceiver;
    private CmdMessageBroadcastReceiver cmdMessageBroadcastReceiver;

    @InjectView(R.id.tpi_footer)
    protected MainTabPageIndicator indicator;

    @InjectView(R.id.vp_pages)
    protected ViewPager pager;

    @InjectView(R.id.layout_notification)
    protected View notification;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Injector.inject(this);

        ButterKnife.inject(this, getView());
        pager.setAdapter(new PagerAdapter(getResources(), getChildFragmentManager()));
        indicator.setViewPager(pager);

        initChatServer();
        initFileUploadService();
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(connectionChangeReceiver);
        getActivity().unregisterReceiver(newMessageBroadcastReceiver);
        getActivity().unregisterReceiver(cmdMessageBroadcastReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe
    public void onResetChatUnreadEvent(ResetChatUnreadEvent event) {
        indicator.getUnreadImage(MainTabPageIndicator.TAB_CHAT)
                .setVisibility(View.INVISIBLE);
    }

    @Subscribe
    public void onResetMessageUnreadEvent(ResetMessageUnreadEvent event) {
        indicator.getUnreadImage(MainTabPageIndicator.TAB_MESSAGE)
                .setVisibility(View.INVISIBLE);
    }

    @Subscribe
    public void onNewMessageEvent(NewMessageEvent event) {
        indicator.getUnreadImage(MainTabPageIndicator.TAB_MESSAGE)
                .setVisibility(View.VISIBLE);
    }

    private void initFileUploadService() {
        String currentUserId = apiKeyProvider.getAuthUserId();
        fileUploadService.init(currentUserId);
    }

    private void initChatServer() {
        connectionChangeReceiver = new ConnectionChangeReceiver();
        IntentFilter connectionChangeIntentFilter = new IntentFilter();
        connectionChangeIntentFilter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(connectionChangeReceiver, connectionChangeIntentFilter);

        newMessageBroadcastReceiver = new NewMessageBroadcastReceiver();
        IntentFilter newMessageIntentFilter = new IntentFilter(chatService.getNewMessageBroadcastAction());
        newMessageIntentFilter.setPriority(NewMessageBroadcastReceiver.PRIORITY);
        getActivity().registerReceiver(newMessageBroadcastReceiver, newMessageIntentFilter);

        cmdMessageBroadcastReceiver = new CmdMessageBroadcastReceiver();
        IntentFilter cmdMessageIntentFilter = new IntentFilter(chatService.getCmdMessageBroadcastAction());
        cmdMessageIntentFilter.setPriority(CmdMessageBroadcastReceiver.PRIORITY);
        getActivity().registerReceiver(cmdMessageBroadcastReceiver, cmdMessageIntentFilter);

        chatService.setConnectionListener(new ChatConnectionListener(getActivity()));
        chatService.setGroupChangeListener(new GroupChangeListener(getActivity(), bus));
        chatService.setMessageNotifyListener(new MessageNotifyListener(getActivity()));
        chatService.setNotificationClickListener(new NotificationClickListener(getActivity()));
        chatService.setContactListener(new ContactListener(bus));
        chatService.setAppInitialized();
        chatService.loadAllResources();
    }

    private class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                hideNotification();
            } else {
                showNoNetworkError();
            }
        }
    }

    private void hideNotification() {
        Animation.fadeOut(notification, Animation.Duration.SHORT);
    }

    private void showNoNetworkError() {
        Animation.fadeIn(notification, Animation.Duration.SHORT);
        TextView notificationText = (TextView) notification.findViewById(R.id.text_notification);
        notificationText.setText(R.string.error_no_network);
    }

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        public static final int PRIORITY = 3;

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
            if (pager.getCurrentItem() != MainTabPageIndicator.TAB_CHAT) {
                indicator.getUnreadImage(MainTabPageIndicator.TAB_CHAT)
                        .setVisibility(View.VISIBLE);
            } else {
                bus.post(new NewChatMessageEvent());
            }
        }
    }

    private class CmdMessageBroadcastReceiver extends BroadcastReceiver {

        public static final int PRIORITY = 3;

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();

            try {
                EMMessage message = intent.getParcelableExtra("message");
                CmdMessage cmdMessage = chatService.getCmdMessage(message);
                switch (cmdMessage.getType()) {
                    case CmdMessage.Type.GROUP_JOIN:
                        handleGroupJoinCmdMessage(cmdMessage);
                        break;
                    case CmdMessage.Type.GROUP_QUIT:
                        handleGroupQuitCmdMessage(cmdMessage);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                Ln.e(e);
            }

            return;
        }
    }

    private void handleGroupJoinCmdMessage(CmdMessage cmdMessage) {
        String groupId = cmdMessage.getPayload();
        String userId = cmdMessage.getContent();
        chatService.addGroupMember(groupId, userId);
    }

    private void handleGroupQuitCmdMessage(CmdMessage cmdMessage) {
        String groupId = cmdMessage.getPayload();
        String userId = cmdMessage.getContent();
        chatService.removeGroupMember(groupId, userId);
    }
}