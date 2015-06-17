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
import com.aumum.app.mobile.core.dao.MessageStore;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.events.NewMessageEvent;
import com.aumum.app.mobile.events.NewChatMessageEvent;
import com.aumum.app.mobile.events.ResetChatUnreadEvent;
import com.aumum.app.mobile.events.ResetHomeUnreadEvent;
import com.aumum.app.mobile.events.ResetMessageUnreadEvent;
import com.aumum.app.mobile.ui.contact.ContactListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.EMChatUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Fragment which houses the View pager.
 */
public class MainFragment extends Fragment implements EMEventListener {

    @Inject UserStore userStore;
    @Inject MomentStore momentStore;
    @Inject MessageStore messageStore;
    @Inject FileUploadService fileUploadService;
    @Inject ApiKeyProvider apiKeyProvider;
    @Inject Bus bus;

    private Date lastUpdate;
    private ConnectionChangeReceiver connectionChangeReceiver;

    @InjectView(R.id.tpi_footer)
    protected MainTabPageIndicator indicator;

    @InjectView(R.id.vp_pages)
    protected ViewPager pager;

    @InjectView(R.id.layout_notification)
    protected View notification;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Injector.inject(this);

        ButterKnife.inject(this, getView());
        pager.setAdapter(new PagerAdapter(getResources(), getChildFragmentManager()));
        indicator.setViewPager(pager);

        initConnectivity();
        initChatService();
        initFileUploadService();
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        if (lastUpdate == null) {
            updateDiscoveryList();
        } else {
            Date now = new Date();
            long duration = now.getTime() - lastUpdate.getTime();
            if (duration >= 1200000) {
                updateDiscoveryList();
            }
        }
        EMChatUtils.pushActivity(getActivity());
        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{
                        EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventNewCMDMessage
                });
        handleCmdMessageQueue();
    }

    @Override
    public void onPause() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void onStop() {
        EMChatManager.getInstance().unregisterEventListener(this);
        EMChatUtils.popActivity(getActivity());
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(connectionChangeReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void updateDiscoveryList() {
        new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                momentStore.getLatestList();
                momentStore.getHottestList();
                userStore.getTalentList();
                if (lastUpdate == null) {
                    User currentUser = userStore.getCurrentUser();
                    userStore.getListByCity(
                            currentUser.getObjectId(), currentUser.getCity());
                }
                lastUpdate = new Date();
                return true;
            }
        }.execute();
    }

    @Subscribe
    public void onResetHomeUnreadEvent(ResetHomeUnreadEvent event) {
        updateTabUnread(MainTabPageIndicator.TAB_HOME, View.INVISIBLE);
    }

    @Subscribe
    public void onResetChatUnreadEvent(ResetChatUnreadEvent event) {
        updateTabUnread(MainTabPageIndicator.TAB_CHAT, View.INVISIBLE);
    }

    @Subscribe
    public void onResetMessageUnreadEvent(ResetMessageUnreadEvent event) {
        updateTabUnread(MainTabPageIndicator.TAB_MESSAGE, View.INVISIBLE);
    }

    @Subscribe
    public void onNewMessageEvent(NewMessageEvent event) {
        updateTabUnread(MainTabPageIndicator.TAB_MESSAGE, View.VISIBLE);
    }

    private void initConnectivity() {
        connectionChangeReceiver = new ConnectionChangeReceiver();
        IntentFilter connectionChangeIntentFilter = new IntentFilter();
        connectionChangeIntentFilter.addAction(
                android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(
                connectionChangeReceiver, connectionChangeIntentFilter);
    }

    private void initFileUploadService() {
        String currentUserId = apiKeyProvider.getAuthUserId();
        fileUploadService.init(currentUserId);
    }

    private void initChatService() {
        EMChatUtils.setContactListener(new ContactListener(getActivity(), bus));
        EMChatUtils.loadAllResources();
        EMChatUtils.setAppInitialized();
    }

    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
                handleNewMessage();
                break;
            case EventNewCMDMessage:
                EMMessage message = (EMMessage) event.getData();
                CmdMessage cmdMessage = EMChatUtils.getCmdMessage(message);
                if (cmdMessage != null) {
                    handleNewCmdMessage(cmdMessage);
                }
                break;
            default:
                break;
        }
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

    private void handleNewMessage() {
        bus.post(new NewChatMessageEvent());
        if (pager.getCurrentItem() != MainTabPageIndicator.TAB_CHAT) {
            updateTabUnread(MainTabPageIndicator.TAB_CHAT, View.VISIBLE);
        }
    }

    private void handleNewCmdMessage(CmdMessage cmdMessage) {
        switch (cmdMessage.getType()) {
            case CmdMessage.Type.NEW_MOMENT:
                handleNewMomentCmdMessage();
                break;
            case CmdMessage.Type.MOMENT_LIKE:
                handleMomentLikeCmdMessage(cmdMessage);
                break;
            case CmdMessage.Type.MOMENT_COMMENT:
                handleMomentCommentCmdMessage(cmdMessage);
                break;
            default:
                break;
        }
    }

    private void handleCmdMessageQueue() {
        for (CmdMessage cmdMessage: EMChatUtils.getCmdMessageQueue()) {
            handleNewCmdMessage(cmdMessage);
        }
        EMChatUtils.clearCmdMessageQueue();
    }

    private void updateTabUnread(final int tab,
                                 final int visibility) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                indicator.getUnreadImage(tab).setVisibility(visibility);
            }
        });
    }

    private void handleNewMomentCmdMessage() {
        updateTabUnread(MainTabPageIndicator.TAB_HOME, View.VISIBLE);
    }

    private void handleMomentLikeCmdMessage(CmdMessage cmdMessage) {
        String momentId = cmdMessage.getPayload();
        String userId = cmdMessage.getContent();
        messageStore.addMomentLike(momentId, userId);
        updateTabUnread(MainTabPageIndicator.TAB_MESSAGE, View.VISIBLE);
    }

    private void handleMomentCommentCmdMessage(CmdMessage cmdMessage) {
        String momentId = cmdMessage.getPayload();
        String userId = cmdMessage.getContent();
        String comment = cmdMessage.getTitle();
        messageStore.addMomentComment(momentId, userId, comment);
        updateTabUnread(MainTabPageIndicator.TAB_MESSAGE, View.VISIBLE);
    }
}