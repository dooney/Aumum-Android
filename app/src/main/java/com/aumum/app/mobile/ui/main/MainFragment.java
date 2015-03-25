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
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.PartyRequestStore;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.NotificationService;
import com.aumum.app.mobile.core.service.ScheduleService;
import com.aumum.app.mobile.events.NewChatMessageEvent;
import com.aumum.app.mobile.events.ResetDiscoveryUnreadEvent;
import com.aumum.app.mobile.events.ResetChatUnreadEvent;
import com.aumum.app.mobile.events.ResetPartyRequestUnreadEvent;
import com.aumum.app.mobile.events.ResetPartyUnreadEvent;
import com.aumum.app.mobile.ui.chat.ChatConnectionListener;
import com.aumum.app.mobile.ui.chat.GroupChangeListener;
import com.aumum.app.mobile.ui.chat.MessageNotifyListener;
import com.aumum.app.mobile.ui.chat.NotificationClickListener;
import com.aumum.app.mobile.ui.contact.ContactListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.UpYunUtils;
import com.easemob.chat.EMMessage;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

/**
 * Fragment which houses the View pager.
 */
public class MainFragment extends Fragment
        implements ScheduleService.OnScheduleListener {

    @Inject PartyStore partyStore;
    @Inject PartyRequestStore partyRequestStore;
    @Inject MomentStore momentStore;
    @Inject NotificationService notificationService;
    @Inject ChatService chatService;
    @Inject ApiKeyProvider apiKeyProvider;
    @Inject Bus bus;

    private ScheduleService scheduleService;
    private SafeAsyncTask<Boolean> task;

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
        initImageServer();
        initScheduleService();
    }

    @Override
    public void onResume() {
        super.onResume();
        scheduleService.start();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onDestroy();
        scheduleService.shutDown();
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

    @Override
    public void onAction() {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                String currentUserId = apiKeyProvider.getAuthUserId();
                int unreadCount = partyStore.getUnreadCount(currentUserId);
                if (unreadCount > 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            indicator.getUnreadImage(MainTabPageIndicator.TAB_PARTY)
                                    .setVisibility(View.VISIBLE);
                        }
                    });
                }
                unreadCount = partyRequestStore.getUnreadCount();
                if (unreadCount > 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            indicator.getUnreadImage(MainTabPageIndicator.TAB_PARTY)
                                    .setVisibility(View.VISIBLE);
                        }
                    });
                }
                unreadCount = momentStore.getUnreadCount();
                if (unreadCount > 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            indicator.getUnreadImage(MainTabPageIndicator.TAB_DISCOVERY)
                                    .setVisibility(View.VISIBLE);
                        }
                    });
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }

    @Subscribe
    public void onResetPartyUnreadEvent(ResetPartyUnreadEvent event) {
        indicator.getUnreadImage(MainTabPageIndicator.TAB_PARTY)
                .setVisibility(View.INVISIBLE);
    }

    @Subscribe
    public void onResetPartyRequestUnreadEvent(ResetPartyRequestUnreadEvent event) {
        indicator.getUnreadImage(MainTabPageIndicator.TAB_PARTY)
                .setVisibility(View.INVISIBLE);
    }

    @Subscribe
    public void onResetDiscoveryUnreadEvent(ResetDiscoveryUnreadEvent event) {
        indicator.getUnreadImage(MainTabPageIndicator.TAB_DISCOVERY)
                .setVisibility(View.INVISIBLE);
    }

    @Subscribe
    public void onResetChatUnreadEvent(ResetChatUnreadEvent event) {
        indicator.getUnreadImage(MainTabPageIndicator.TAB_CHAT)
                .setVisibility(View.INVISIBLE);
    }

    private void initImageServer() {
        String currentUserId = apiKeyProvider.getAuthUserId();
        UpYunUtils.setCurrentDir(currentUserId);
    }

    private void initScheduleService() {
        scheduleService = new ScheduleService(this);
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
        chatService.setGroupChangeListener(new GroupChangeListener(getActivity()));
        chatService.setMessageNotifyListener(new MessageNotifyListener(getActivity()));
        chatService.setNotificationClickListener(new NotificationClickListener(getActivity()));
        chatService.setContactListener(new ContactListener());
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
                    case CmdMessage.Type.PARTY_NEW:
                    case CmdMessage.Type.PARTY_JOIN:
                    case CmdMessage.Type.PARTY_QUIT:
                    case CmdMessage.Type.PARTY_LIKE:
                    case CmdMessage.Type.PARTY_CANCEL:
                        handlePartyDetailsCmdMessage(cmdMessage);
                        break;
                    case CmdMessage.Type.PARTY_COMMENT:
                    case CmdMessage.Type.PARTY_REPLY:
                    case CmdMessage.Type.PARTY_COMMENT_LIKE:
                        handlePartyCommentsCmdMessage(cmdMessage);
                    case CmdMessage.Type.GROUP_JOIN:
                        handleGroupJoinCmdMessage(cmdMessage, message.getFrom());
                        break;
                    case CmdMessage.Type.GROUP_QUIT:
                        handleGroupQuitCmdMessage(cmdMessage, message.getFrom());
                        break;
                    case CmdMessage.Type.ASKING_REPLY:
                    case CmdMessage.Type.ASKING_REPLIED:
                    case CmdMessage.Type.ASKING_NEW:
                    case CmdMessage.Type.ASKING_LIKE:
                    case CmdMessage.Type.ASKING_REPLY_LIKE:
                        break;
                    case CmdMessage.Type.USER_NEW:
                        handleUserDetailsCmdMessage(cmdMessage);
                        break;
                    case CmdMessage.Type.MOMENT_NEW:
                    case CmdMessage.Type.MOMENT_LIKE:
                    case CmdMessage.Type.MOMENT_COMMENT:
                    case CmdMessage.Type.MOMENT_REPLY:
                    case CmdMessage.Type.MOMENT_COMMENT_LIKE:
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

    private void handlePartyDetailsCmdMessage(CmdMessage cmdMessage) {
        String partyId = cmdMessage.getPayload();
        String title = cmdMessage.getTitle();
        String content = cmdMessage.getContent();
        notificationService.pushPartyDetailsNotification(partyId, title, content);
    }

    private void handlePartyCommentsCmdMessage(CmdMessage cmdMessage) {
        String partyId = cmdMessage.getPayload();
        String title = cmdMessage.getTitle();
        String content = cmdMessage.getContent();
        notificationService.pushPartyCommentsNotification(partyId, title, content);
    }

    private void handleGroupJoinCmdMessage(CmdMessage cmdMessage, String userId) {
        String groupId = cmdMessage.getPayload();
        chatService.addGroupMember(groupId, userId);
    }

    private void handleGroupQuitCmdMessage(CmdMessage cmdMessage, String userId) {
        String groupId = cmdMessage.getPayload();
        chatService.removeGroupMember(groupId, userId);
    }

    private void handleAskingDetailsCmdMessage(CmdMessage cmdMessage) {
        String askingId = cmdMessage.getPayload();
        String title = cmdMessage.getTitle();
        String content = cmdMessage.getContent();
        notificationService.pushAskingDetailsNotification(askingId, title, content);
    }

    private void handleUserDetailsCmdMessage(CmdMessage cmdMessage) {
        String userId = cmdMessage.getPayload();
        String title = cmdMessage.getTitle();
        String content = cmdMessage.getContent();
        notificationService.pushUserDetailsNotification(userId, title, content);
    }

    private void handleMomentDetailsCmdMessage(CmdMessage cmdMessage) {
        String momentId = cmdMessage.getPayload();
        String title = cmdMessage.getTitle();
        String content = cmdMessage.getContent();
        notificationService.pushMomentDetailsNotification(momentId, title, content);
    }
}