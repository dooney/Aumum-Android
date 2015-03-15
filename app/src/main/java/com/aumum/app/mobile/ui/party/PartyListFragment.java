package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.events.ResetPartyUnreadEvent;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;
import com.aumum.app.mobile.utils.GPSTracker;
import com.aumum.app.mobile.utils.Ln;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PartyListFragment extends RefreshItemListFragment<Card>
        implements PartyCommentClickListener {

    @Inject UserStore userStore;
    @Inject PartyStore partyStore;
    @Inject Bus bus;

    protected User currentUser;
    protected List<Party> dataSet = new ArrayList<Party>();

    protected GPSTracker gpsTracker;
    private ViewGroup container;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        gpsTracker = new GPSTracker(getActivity());
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        this.container = container;
        return inflater.inflate(R.layout.fragment_party_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reload();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);

        if (container.getTag() != null) {
            int requestCode = (Integer) container.getTag();
            if (requestCode == Constants.RequestCode.NEW_PARTY_REQ_CODE) {
                refresh(null);
                container.setTag(null);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RequestCode.GET_PARTY_DETAILS_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            String partyId = data.getStringExtra(PartyDetailsActivity.INTENT_PARTY_ID);
            if (data.hasExtra(PartyDetailsActivity.INTENT_DELETED)) {
                onPartyDeleted(partyId);
            } else {
                onPartyRefresh(partyId);
            }
        } else if (requestCode == Constants.RequestCode.GET_PARTY_COMMENTS_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            String partyId = data.getStringExtra(PartyCommentsActivity.INTENT_PARTY_ID);
            onPartyRefresh(partyId);
        }
    }

    @Override
    protected ArrayAdapter<Card> createAdapter(List<Card> items) {
        return new CardArrayAdapter(getActivity(), items);
    }

    @Override
    protected List<Card> loadDataCore(final Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        return super.loadDataCore(bundle);
    }

    @Override
    protected void getUpwardsList() throws Exception {
        String after = null;
        if (dataSet.size() > 0) {
            after = dataSet.get(0).getCreatedAt();
        }
        List<Party> partyList = onGetUpwardsList(after);
        Collections.reverse(partyList);
        for(Party party: partyList) {
            dataSet.add(0, party);
        }
        bus.post(new ResetPartyUnreadEvent());
    }

    @Override
    protected void getBackwardsList() throws Exception {
        if (dataSet.size() > 0) {
            Party last = dataSet.get(dataSet.size() - 1);
            List<Party> partyList = onGetBackwardsList(last.getCreatedAt());
            dataSet.addAll(partyList);
            if (partyList.size() > 0) {
                setMore(true);
            } else {
                setMore(false);
            }
        }
    }

    private Card buildCard(Party party, String currentUserId) throws Exception {
        if (party.getUser() == null) {
            party.setUser(userStore.getUserById(party.getUserId()));
        }
        gpsTracker.getLocation();
        party.setDistance(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        Card card = new PartyCard(getActivity(), party, currentUserId, this);
        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                PartyCard partyCard = (PartyCard) card;
                final Intent intent = new Intent(getActivity(), PartyDetailsActivity.class);
                intent.putExtra(PartyDetailsActivity.INTENT_PARTY_ID, partyCard.getParty().getObjectId());
                startActivityForResult(intent, Constants.RequestCode.GET_PARTY_DETAILS_REQ_CODE);
            }
        });
        return card;
    }

    @Override
    protected List<Card> buildCards() throws Exception {
        int totalCount = dataSet.size();
        if (totalCount < PartyStore.LIMIT_PER_LOAD) {
            setMore(false);
        }
        List<Card> cards = new ArrayList<Card>();
        if (totalCount > 0) {
            for (Party party : dataSet) {
                Card card = buildCard(party, currentUser.getObjectId());
                cards.add(card);
            }
        }
        return cards;
    }

    private void onPartyDeleted(String partyId) {
        try {
            List<Card> cardList = getData();
            for (Iterator<Card> it = cardList.iterator(); it.hasNext();) {
                Card card = it.next();
                Party party = ((PartyCard) card).getParty();
                if (party.getObjectId().equals(partyId)) {
                    dataSet.remove(party);
                    it.remove();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getListAdapter().notifyDataSetChanged();
                        }
                    });
                    return;
                }
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    private void onPartyRefresh(String partyId) {
        try {
            for (int i = 0; i < dataSet.size(); i++) {
                Party item = dataSet.get(i);
                if (item.getObjectId().equals(partyId)) {
                    Party party = partyStore.getPartyById(partyId);
                    getData().set(i, buildCard(party, currentUser.getObjectId()));
                    dataSet.set(i, party);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getListAdapter().notifyDataSetChanged();
                        }
                    });
                    return;
                }
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    private List<Party> onGetUpwardsList(String after) throws Exception {
        return partyStore.getUpwardsList(currentUser.getObjectId(), after);
    }

    private List<Party> onGetBackwardsList(String before) throws Exception {
        return partyStore.getBackwardsList(currentUser.getObjectId(), before);
    }

    @Override
    public void OnClick(String partyId) {
        final Intent intent = new Intent(getActivity(), PartyCommentsActivity.class);
        intent.putExtra(PartyCommentsActivity.INTENT_PARTY_ID, partyId);
        startActivityForResult(intent, Constants.RequestCode.GET_PARTY_COMMENTS_REQ_CODE);
    }
}
