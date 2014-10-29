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
import com.aumum.app.mobile.core.dao.PartyReasonStore;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.PartyReason;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.MessageListener;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.AddPartyReasonEvent;
import com.aumum.app.mobile.events.AddPartyReasonFinishedEvent;
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
public class PartyReasonsFragment extends ItemListFragment<PartyReason> {

    @Inject RestService service;
    @Inject MessageListener messageListener;
    @Inject Bus bus;

    private SafeAsyncTask<Boolean> task;

    private String partyId;
    private Party party;
    private User currentUser;
    private PartyReasonStore partyReasonStore;
    private PartyStore partyStore;
    private UserStore userStore;

    private ViewGroup mainView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        partyStore = new PartyStore(getActivity());
        userStore = UserStore.getInstance(getActivity());
        partyReasonStore = new PartyReasonStore();
        final Intent intent = getActivity().getIntent();
        partyId = intent.getStringExtra(PartyDetailsActivity.INTENT_PARTY_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_party_reasons, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainView = (ViewGroup) view.findViewById(R.id.main_view);
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
    protected List<PartyReason> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser(false);
        party = partyStore.getPartyById(partyId);
        return partyReasonStore.getPartyReasons(partyId);
    }

    @Override
    protected void handleLoadResult(List<PartyReason> result) {
        try {
            if (result != null) {
                for (PartyReason reason : result) {
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
    protected ArrayAdapter<PartyReason> createAdapter(List<PartyReason> items) {
        return new ReasonsAdapter(getActivity(), items);
    }

    @Subscribe
    public void onAddPartyReasonEvent(final AddPartyReasonEvent event) {
        if (task != null) {
            return;
        }

        // update UI first
        PartyReason reason = new PartyReason();
        reason.setType(event.getType());
        reason.setContent(event.getReason());
        reason.setUserId(currentUser.getObjectId());
        reason.setUser(currentUser);
        getData().add(0, reason);
        getListAdapter().notifyDataSetChanged();
        show();
        scrollToTop();

        // submit
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                PartyReason reason = getData().get(0);
                final PartyReason newReason = new PartyReason();
                newReason.setType(reason.getType());
                newReason.setContent(reason.getContent());
                newReason.setUserId(reason.getUserId());

                // join reason
                PartyReason response = service.newPartyReason(newReason);
                service.addPartyReasons(partyId, response.getObjectId());
                reason.setObjectId(response.getObjectId());
                reason.setCreatedAt(response.getCreatedAt());
                party.getReasons().add(response.getObjectId());

                User currentUser = userStore.getCurrentUser(false);
                if (reason.getType() == PartyReason.JOIN) {
                    service.addPartyMember(partyId, currentUser.getObjectId());
                    service.addUserParty(currentUser.getObjectId(), partyId);
                    currentUser.getParties().add(partyId);
                    party.getMembers().add(currentUser.getObjectId());

                    messageListener.onMessageEvent(new MessageEvent(Message.JOIN, party.getUserId(), currentUser.getObjectId()));
                } else if (reason.getType() == PartyReason.QUIT) {
                    service.removePartyMember(partyId, currentUser.getObjectId());
                    service.removeUserParty(currentUser.getObjectId(), partyId);
                    currentUser.getParties().remove(partyId);
                    party.getMembers().remove(currentUser.getObjectId());

                    messageListener.onMessageEvent(new MessageEvent(Message.QUIT, party.getUserId(), currentUser.getObjectId()));
                }
                userStore.saveUser(currentUser);
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
                bus.post(new AddPartyReasonFinishedEvent(event.getType()));
            }
        };
        task.execute();
    }

    @Override
    protected View getMainView() {
        return mainView;
    }
}