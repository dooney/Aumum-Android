package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

public class SearchPartyFragment extends ItemListFragment<Card>
        implements PartyActionListener.OnActionListener,
        PartyDetailsListener {

    private PartyStore dataStore;

    private UserStore userStore;

    private GPSTracker gpsTracker;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userStore = UserStore.getInstance(getActivity());
        dataStore = new PartyStore();
        gpsTracker = new GPSTracker(getActivity());
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_party, null);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_parties);
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
                onPartyDeletedSuccess(partyId);
            }
        }
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_parties;
    }

    @Override
    protected List<Card> loadDataCore(Bundle bundle) throws Exception {
        List<Party> partyList = dataStore.getLiveList();
        if (partyList != null) {
            gpsTracker.getLocation();
            for (Party party : partyList) {
                User user = userStore.getUserById(party.getUserId(), false);
                party.setUser(user);
                party.setDistance(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                if (!party.isNearby()) {
                    partyList.remove(party);
                }
            }
        }
        return buildCards(partyList);
    }

    private List<Card> buildCards(List<Party> partyList) {
        List<Card> cards = new ArrayList<Card>();
        if (partyList.size() > 0) {
            User user = userStore.getCurrentUser(false);
            for (Party party : partyList) {
                Card card = new PartyCard(getActivity(), party, user.getObjectId(), this, this);
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

    @Override
    public void onPartyDeletedSuccess(String partyId) {
        try {
            List<Card> cardList = getData();
            for (Card card : cardList) {
                Party party = ((PartyCard) card).getParty();
                if (party.getObjectId().equals(partyId)) {
                    cardList.remove(card);
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

    @Override
    public void onPartySharedSuccess() {

    }

    @Override
    public void onPartyDetails(String partyId) {
        final Intent intent = new Intent(getActivity(), PartyDetailsActivity.class);
        intent.putExtra(PartyDetailsActivity.INTENT_PARTY_ID, partyId);
        startActivityForResult(intent, Constants.RequestCode.GET_PARTY_DETAILS_REQ_CODE);
    }
}
