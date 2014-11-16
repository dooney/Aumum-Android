package com.aumum.app.mobile.ui.main;

import android.content.Intent;
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
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ScheduleService;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
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

    private ScheduleService scheduleService;
    private SafeAsyncTask<Boolean> task;

    @InjectView(R.id.tpi_footer)
    protected TabPageIndicator indicator;

    @InjectView(R.id.vp_pages)
    protected ViewPager pager;

    private int landingPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Injector.inject(this);

        scheduleService = new ScheduleService(this);

        ButterKnife.inject(this, getView());
        pager.setAdapter(new PagerAdapter(getResources(), getChildFragmentManager()));
        indicator.setViewPager(pager);
        pager.setCurrentItem(landingPage);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setLandingPage(int page) {
        landingPage = page;
    }

    @Override
    public void onAction() {
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUserFromServer();
                List<Message> unreadMessageList = messageStore.getUnreadListFromServer(currentUser.getMessages());
                if (unreadMessageList.size() > 0) {
                    messageStore.getUnreadList().addAll(unreadMessageList);
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
}