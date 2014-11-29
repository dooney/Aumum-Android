package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.utils.GPSTracker;
import com.aumum.app.mobile.utils.Ln;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

public class SearchPartyFragment extends ItemListFragment<Card> {

    @Inject UserStore userStore;
    @Inject PartyStore dataStore;

    private GPSTracker gpsTracker;

    private int mode;
    private String userId;
    private final int NEARBY_PARTIES = 0;
    private final int USER_PARTIES = 1;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        gpsTracker = new GPSTracker(getActivity());
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }

        final Intent intent = getActivity().getIntent();
        if (intent.hasExtra(SearchPartyActivity.INTENT_NEARBY_PARTIES)) {
            mode = NEARBY_PARTIES;
        }
        userId = intent.getStringExtra(SearchPartyActivity.INTENT_USER_ID);
        if (userId != null) {
            mode = USER_PARTIES;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_party, null);
    }

    @Override
    protected ArrayAdapter<Card> createAdapter(List<Card> items) {
        return new CardArrayAdapter(getActivity(), items);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RequestCode.GET_PARTY_DETAILS_REQ_CODE && resultCode == Activity.RESULT_OK) {
            String partyId = data.getStringExtra(PartyDetailsActivity.INTENT_PARTY_ID);
            if (partyId != null) {
                onPartyDeleted(partyId);
            }
        }
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_parties;
    }

    @Override
    protected List<Card> loadDataCore(Bundle bundle) throws Exception {
        List<Party> partyList = null;
        switch (mode) {
            case NEARBY_PARTIES:
                partyList = getNearByParties();
                break;
            case USER_PARTIES:
                partyList = getUserParties();
                break;
            default:
                break;
        }
        return buildCards(partyList);
    }

    private List<Party> getNearByParties() throws Exception {
        List<Party> partyList = dataStore.getLiveListFromServer();
        if (partyList != null) {
            gpsTracker.getLocation();
            for (Iterator<Party> it = partyList.iterator(); it.hasNext();) {
                Party party = it.next();
                party.setDistance(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                if (!party.isNearby()) {
                    it.remove();
                }
            }
        }
        return partyList;
    }

    private List<Party> getUserParties() throws Exception {
        User user = userStore.getUserById(userId);
        List<Party> partyList = dataStore.getList(user.getParties());
        return partyList;
    }

    private List<Card> buildCards(List<Party> partyList) throws Exception {
        List<Card> cards = new ArrayList<Card>();
        if (partyList.size() > 0) {
            User currentUser = userStore.getCurrentUser();
            for (Party party : partyList) {
                if (party.getUser() == null) {
                    party.setUser(userStore.getUserById(party.getUserId()));
                }
                Card card = new PartyCard(getActivity(), party, currentUser.getObjectId());
                card.setOnClickListener(new Card.OnCardClickListener() {
                    @Override
                    public void onClick(Card card, View view) {
                        PartyCard partyCard = (PartyCard) card;
                        final Intent intent = new Intent(getActivity(), PartyDetailsActivity.class);
                        intent.putExtra(PartyDetailsActivity.INTENT_PARTY_ID, partyCard.getParty().getObjectId());
                        startActivityForResult(intent, Constants.RequestCode.GET_PARTY_DETAILS_REQ_CODE);
                    }
                });
                cards.add(card);
            }
        }
        return cards;
    }

    @Override
    protected void handleLoadResult(List<Card> result) {
        try {
            if (result != null) {
                getData().clear();
                getData().addAll(result);
                getListAdapter().notifyDataSetChanged();
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    private void onPartyDeleted(String partyId) {
        try {
            List<Card> cardList = getData();
            for (Iterator<Card> it = cardList.iterator(); it.hasNext();) {
                Card card = it.next();
                Party party = ((PartyCard) card).getParty();
                if (party.getObjectId().equals(partyId)) {
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
}
