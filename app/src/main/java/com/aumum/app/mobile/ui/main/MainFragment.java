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
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.CmdMessage;
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
import com.easemob.chat.EMMessage;
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
    @Inject PartyStore partyStore;
    @Inject NotificationService notificationService;
    @Inject ChatService chatService;
    @Inject ApiKeyProvider apiKeyProvider;

    private ScheduleService scheduleService;
    private SafeAsyncTask<Boolean> task;

    CmdMessageBroadcastReceiver cmdMessageBroadcastReceiver;

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

        cmdMessageBroadcastReceiver = new CmdMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(chatService.getCmdMessageBroadcastAction());
        intentFilter.setPriority(CmdMessageBroadcastReceiver.PRIORITY);
        getActivity().registerReceiver(cmdMessageBroadcastReceiver, intentFilter);

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
                User currentUser = userStore.getCurrentUserFromServer();
                List<Party> unreadPartyList = partyStore.getUnreadListFromServer(currentUser.getObjectId());
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
                        String partyId = cmdMessage.getPayload();
                        String title = cmdMessage.getTitle();
                        String content = cmdMessage.getContent();
                        notificationService.pushPartyNotification(partyId, title, content);
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
}