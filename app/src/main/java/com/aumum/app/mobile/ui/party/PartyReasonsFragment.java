package com.aumum.app.mobile.ui.party;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.CreditRuleStore;
import com.aumum.app.mobile.core.dao.PartyReasonStore;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.CreditRule;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.PartyReason;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.AddPartyReasonEvent;
import com.aumum.app.mobile.events.AddPartyReasonFinishedEvent;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;
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

    @Inject RestService restService;
    @Inject UserStore userStore;
    @Inject PartyStore partyStore;
    @Inject PartyReasonStore partyReasonStore;
    @Inject CreditRuleStore creditRuleStore;
    @Inject ChatService chatService;
    @Inject Bus bus;

    private SafeAsyncTask<Boolean> task;

    private Party party;
    private User currentUser;

    private ViewGroup mainView;

    public void setParty(Party party) {
        this.party = party;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
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
    protected List<PartyReason> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        List<PartyReason> result = partyReasonStore.getPartyReasons(party.getReasons());
        for (PartyReason reason: result) {
            reason.setUser(userStore.getUserById(reason.getUserId()));
        }
        return result;
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
        PartyReason reason = new PartyReason(event.getType(), event.getReason(), currentUser.getObjectId());
        reason.setUser(currentUser);
        getData().add(0, reason);
        getListAdapter().notifyDataSetChanged();
        show();
        scrollToTop();

        // submit
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                PartyReason reason = getData().get(0);
                final PartyReason newReason = new PartyReason(
                        reason.getType(),
                        reason.getContent(),
                        reason.getUserId());
                PartyReason response = restService.newPartyReason(newReason);
                party.addReason(response.getObjectId());
                String partyId = party.getObjectId();
                if (reason.getType() == PartyReason.JOIN) {
                    restService.joinParty(partyId, currentUser.getObjectId(), response.getObjectId());
                    party.addMember(currentUser.getObjectId());
                    currentUser.addParty(partyId);
                    if (party.getGroupId() != null) {
                        joinPartyGroup();
                    }
                    if (!partyId.equals(currentUser.getObjectId())) {
                        sendJoinMessage();
                    }
                    updateCredit(currentUser, CreditRule.ADD_PARTY_MEMBER);
                } else if (reason.getType() == PartyReason.QUIT) {
                    restService.quitParty(partyId, currentUser.getObjectId(), response.getObjectId());
                    party.removeMember(currentUser.getObjectId());
                    currentUser.removeParty(partyId);
                    if (!partyId.equals(currentUser.getObjectId())) {
                        sendQuitMessage();
                    }
                    updateCredit(currentUser, CreditRule.DELETE_PARTY_MEMBER);
                }
                partyStore.save(party);
                userStore.save(currentUser);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(getActivity(), cause.getMessage());
                    }
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
                refresh(null);
                bus.post(new AddPartyReasonFinishedEvent(event.getType(), party));
            }
        };
        task.execute();
    }

    @Override
    protected View getMainView() {
        return mainView;
    }

    private void joinPartyGroup() throws Exception {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                chatService.joinGroup(party.getGroupId(), currentUser.getChatId());
                String text = getActivity().getString(R.string.label_group_joint,
                        currentUser.getScreenName());
                chatService.sendSystemMessage(party.getGroupId(), true, text, null);
                CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.GROUP_JOIN,
                        null, currentUser.getObjectId(), party.getGroupId());
                chatService.sendCmdMessage(party.getGroupId(), cmdMessage, true, null);
                return true;
            }
        }.execute();
    }

    private void sendJoinMessage() throws Exception {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String title = getString(R.string.label_join_party_message,
                        currentUser.getScreenName());
                CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.PARTY_JOIN,
                        title, party.getTitle(), party.getObjectId());
                User partyOwner = userStore.getUserById(party.getUserId());
                chatService.sendCmdMessage(partyOwner.getChatId(), cmdMessage, false, null);
                return true;
            }
        }.execute();
    }

    private void sendQuitMessage() throws Exception {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String title = getString(R.string.label_quit_party_message,
                        currentUser.getScreenName());
                CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.PARTY_QUIT,
                        title, party.getTitle(), party.getObjectId());
                User partyOwner = userStore.getUserById(party.getUserId());
                chatService.sendCmdMessage(partyOwner.getChatId(), cmdMessage, false, null);
                return true;
            }
        }.execute();
    }

    private void updateCredit(User currentUser, int seq) throws Exception {
        final CreditRule creditRule = creditRuleStore.getCreditRuleBySeq(seq);
        if (creditRule != null) {
            final int credit = creditRule.getCredit();
            restService.updateUserCredit(currentUser.getObjectId(), credit);
            currentUser.updateCredit(credit);
            userStore.save(currentUser);
            if (credit > 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toaster.showShort(getActivity(), getString(R.string.info_got_credit,
                                creditRule.getDescription(), credit));
                    }
                });
            }
        }
    }
}
