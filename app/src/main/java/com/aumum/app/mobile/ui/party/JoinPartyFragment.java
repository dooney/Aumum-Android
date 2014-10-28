package com.aumum.app.mobile.ui.party;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.PartyJoinReasonStore;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.PartyJoinReason;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.MessageListener;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.AddPartyJoinReasonEvent;
import com.aumum.app.mobile.events.AddPartyJoinReasonFinishedEvent;
import com.aumum.app.mobile.events.MessageEvent;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class JoinPartyFragment extends ItemListFragment<PartyJoinReason> {

    @Inject RestService service;
    @Inject MessageListener messageListener;
    @Inject Bus bus;

    private SafeAsyncTask<Boolean> task;

    private String partyId;
    private Party party;
    private User currentUser;
    private PartyJoinReasonStore partyJoinReasonStore;
    private PartyStore partyStore;
    private UserStore userStore;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        partyStore = new PartyStore(getActivity());
        userStore = UserStore.getInstance(getActivity());
        partyJoinReasonStore = new PartyJoinReasonStore();
        final Intent intent = getActivity().getIntent();
        partyId = intent.getStringExtra(PartyDetailsActivity.INTENT_PARTY_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_join_party, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_join_details;
    }

    @Override
    protected List<PartyJoinReason> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser(false);
        party = partyStore.getPartyById(partyId);
        return partyJoinReasonStore.getPartyJoinReasons(partyId);
    }

    @Override
    protected void handleLoadResult(List<PartyJoinReason> result) {
        try {
            if (result != null) {
                for (PartyJoinReason reason : result) {
                    reason.setUser(userStore.getUserById(reason.getUserId(), false));
                }
                getData().addAll(result);
                getListAdapter().notifyDataSetChanged();
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    @Override
    protected ArrayAdapter<PartyJoinReason> createAdapter(List<PartyJoinReason> items) {
        return new JoinReasonsAdapter(getActivity(), items);
    }

    @Subscribe
    public void onAddPartyJoinReasonEvent(AddPartyJoinReasonEvent event) {
        if (task != null) {
            return;
        }

        // update UI first
        PartyJoinReason reason = new PartyJoinReason();
        String content = event.getReason();
        reason.setContent(content);
        reason.setUserId(currentUser.getObjectId());
        reason.setUser(currentUser);
        getData().add(0, reason);
        getListAdapter().notifyDataSetChanged();
        show();
        scrollToTop();

        // submit
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                PartyJoinReason reason = getData().get(0);
                final PartyJoinReason newReason = new PartyJoinReason();
                newReason.setContent(reason.getContent());
                newReason.setUserId(reason.getUserId());

                // join reason
                PartyJoinReason response = service.newPartyJoinReason(newReason);
                service.addPartyJoinReasons(partyId, response.getObjectId());
                reason.setObjectId(response.getObjectId());
                reason.setCreatedAt(response.getCreatedAt());
                party.getJoinReasons().add(response.getObjectId());

                // join
                User currentUser = userStore.getCurrentUser(false);
                service.addPartyMember(partyId, currentUser.getObjectId());
                service.addUserParty(currentUser.getObjectId(), partyId);
                currentUser.getParties().add(partyId);
                party.getMembers().add(currentUser.getObjectId());
                userStore.saveUser(currentUser);

                messageListener.onMessageEvent(new MessageEvent(Message.JOIN, party.getUserId(), currentUser.getObjectId()));
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
                getListAdapter().notifyDataSetChanged();
                show();
                bus.post(new AddPartyJoinReasonFinishedEvent());
            }
        };
        task.execute();
    }
}
