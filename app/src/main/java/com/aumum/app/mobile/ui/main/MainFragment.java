package com.aumum.app.mobile.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MessageStore;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.NotificationService;
import com.aumum.app.mobile.core.service.ScheduleService;
import com.aumum.app.mobile.ui.chat.MessageNotifyListener;
import com.aumum.app.mobile.ui.chat.NotificationClickListener;
import com.aumum.app.mobile.ui.contact.ContactListener;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.UpYunUtils;
import com.viewpagerindicator.TabPageIndicator;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

/**
 * Fragment which houses the View pager.
 */
public class MainFragment extends Fragment
        implements ScheduleService.OnScheduleListener{

    @Inject UserStore userStore;
    @Inject MessageStore messageStore;
    @Inject PartyStore partyStore;
    @Inject NotificationService notificationService;
    @Inject ChatService chatService;
    @Inject ApiKeyProvider apiKeyProvider;

    private ScheduleService scheduleService;
    private SafeAsyncTask<Boolean> task;

    NewMessageBroadcastReceiver newMessageBroadcastReceiver;

    @InjectView(R.id.tpi_footer)
    protected TabPageIndicator indicator;

    @InjectView(R.id.vp_pages)
    protected ViewPager pager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Injector.inject(this);

        newMessageBroadcastReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(chatService.getNewMessageBroadcastAction());
        intentFilter.setPriority(NewMessageBroadcastReceiver.PRIORITY);
        getActivity().registerReceiver(newMessageBroadcastReceiver, intentFilter);

        chatService.setMessageNotifyListener(new MessageNotifyListener(getActivity()));
        chatService.setNotificationClickListener(new NotificationClickListener(getActivity()));
        chatService.setContactListener(new ContactListener());
        chatService.setAppInitialized();
        String currentUserId = apiKeyProvider.getAuthUserId();
        String password = apiKeyProvider.getAuthPassword();
        String chatId = currentUserId.toLowerCase();
        chatService.authenticate(chatId, password);
        UpYunUtils.setCurrentDir(currentUserId);

        scheduleService = new ScheduleService(this);

        ButterKnife.inject(this, getView());
        pager.setAdapter(new PagerAdapter(getResources(), getChildFragmentManager()));
        indicator.setViewPager(pager);
    }

    @Override
    public void onResume() {
        super.onResume();
        scheduleService.start();
    }

    @Override
    public void onPause() {
        super.onDestroy();
        scheduleService.shutDown();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(newMessageBroadcastReceiver);
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
                User currentUser = userStore.getCurrentUserFromServer();
                List<Message> unreadMessageList = messageStore.getUnreadListFromServer(currentUser.getMessages());
                for (Message message: unreadMessageList) {
                    String fromName = userStore.getUserById(message.getFromUserId()).getScreenName();
                    notificationService.pushUserMessageNotification(fromName, message);
                    messageStore.getUnreadList().add(message);
                }
                List<Party> unreadPartyList = partyStore.getUnreadListFromServer();
                if (unreadPartyList.size() > 0) {
                    partyStore.getUnreadList().addAll(unreadPartyList);
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

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {

        public static final int PRIORITY = 3;

        @Override
        public void onReceive(Context context, Intent intent) {
            return;
        }
    }
}