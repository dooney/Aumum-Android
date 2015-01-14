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
import com.aumum.app.mobile.core.model.PlaceRange;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;
import com.aumum.app.mobile.utils.GPSTracker;
import com.aumum.app.mobile.utils.Ln;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

public class SearchPartyFragment extends RefreshItemListFragment<Card> {

    @Inject UserStore userStore;
    @Inject PartyStore partyStore;

    private User user;
    private User currentUser;
    private GPSTracker gpsTracker;
    private List<Party> dataSet;

    private int mode;
    private String userId;
    private final int NEARBY_PARTIES = 0;
    private final int USER_PARTIES = 1;
    private final int FAVORITE_PARTIES = 2;
    private final int LOCATION_NEARBY_PARTIES = 3;

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
        if (intent.hasExtra(SearchPartyActivity.INTENT_LOCATION_NEARBY_PARTIES)) {
            mode = LOCATION_NEARBY_PARTIES;
        }
        userId = intent.getStringExtra(SearchPartyActivity.INTENT_USER_ID);
        if (userId != null) {
            mode = USER_PARTIES;
        }
        boolean isFavorite = intent.getBooleanExtra(SearchPartyActivity.INTENT_IS_FAVORITE, false);
        if (isFavorite) {
            mode = FAVORITE_PARTIES;
        }

        dataSet = new ArrayList<Party>();
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
    protected String getErrorMessage(Exception exception) {
        return getString(R.string.error_load_parties);
    }

    @Override
    protected List<Card> loadDataCore(Bundle bundle) throws Exception {
        if (userId != null) {
            user = userStore.getUserById(userId);
        }
        currentUser = userStore.getCurrentUser();
        return super.loadDataCore(bundle);
    }

    @Override
    protected void getUpwardsList() throws Exception {
        switch (mode) {
            case NEARBY_PARTIES:
                getNearByParties(null);
                break;
            case USER_PARTIES:
                getUserParties();
                break;
            case FAVORITE_PARTIES:
                getUserFavoriteParties();
                break;
            case LOCATION_NEARBY_PARTIES:
                getLocationNearByParties(null);
                break;
            default:
                throw new Exception("Invalid mode: " + mode);
        }
    }

    @Override
    protected void getBackwardsList() throws Exception {
        if (dataSet.size() > 0) {
            Party last = dataSet.get(dataSet.size() - 1);
            switch (mode) {
                case NEARBY_PARTIES:
                    getNearByParties(last.getCreatedAt());
                    break;
                case USER_PARTIES:
                    getUserPartiesBefore(last.getCreatedAt());
                    break;
                case FAVORITE_PARTIES:
                    getUserFavoritePartiesBefore(last.getCreatedAt());
                    break;
                case LOCATION_NEARBY_PARTIES:
                    getLocationNearByParties(last.getCreatedAt());
                    break;
                default:
                    throw new Exception("Invalid mode: " + mode);
            }
        }
    }

    private void getNearByPartiesCore(String time,
                                      double latitude,
                                      double longitude) throws Exception {
        PlaceRange range = new PlaceRange(latitude, longitude);
        List<Party> partyList = partyStore.getNearByList(currentUser.getObjectId(), range, time);
        if (partyList.size() > 0) {
            dataSet.addAll(partyList);
            setMore(true);
        } else {
            setMore(false);
        }
    }

    private void getNearByParties(String time) throws Exception {
        gpsTracker.getLocation();
        getNearByPartiesCore(time, gpsTracker.getLatitude(), gpsTracker.getLongitude());
    }

    private void getLocationNearByParties(String time) throws Exception {
        gpsTracker.getLocation();
        final Intent intent = getActivity().getIntent();
        double latitude = intent.getDoubleExtra(SearchPartyActivity.INTENT_LOCATION_LAT,
                gpsTracker.getLatitude());
        double longitude = intent.getDoubleExtra(SearchPartyActivity.INTENT_LOCATION_LNG,
                gpsTracker.getLongitude());
        getNearByPartiesCore(time, latitude, longitude);
    }

    private void getUserParties() throws Exception {
        List<Party> partyList = partyStore.getRecentList(userId, user.getParties());
        dataSet.addAll(partyList);
    }

    private void getUserPartiesBefore(String time) throws Exception {
        getPartiesBefore(user.getParties(), time);
    }

    private void getUserFavoriteParties() throws Exception {
        List<Party> partyList = partyStore.getRecentList(userId, user.getFavParties());
        dataSet.addAll(partyList);
    }

    private void getUserFavoritePartiesBefore(String time) throws Exception {
        getPartiesBefore(user.getFavParties(), time);
    }

    private void getPartiesBefore(List<String> idList, String time) throws Exception {
        List<Party> partyList = partyStore.getBackwardsList(idList, time);
        if (partyList.size() > 0) {
            dataSet.addAll(partyList);
            setMore(true);
        } else {
            setMore(false);
        }
    }

    private Card buildCard(Party party, String currentUserId) throws Exception {
        if (party.getUser() == null) {
            party.setUser(userStore.getUserById(party.getUserId()));
        }
        gpsTracker.getLocation();
        party.setDistance(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        Card card = new PartyCard(SearchPartyFragment.this, party, currentUserId);
        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                PartyCard partyCard = (PartyCard) card;
                final Intent intent = new Intent(getActivity(), PartyDetailsActivity.class);
                intent.putExtra(PartyDetailsActivity.INTENT_PARTY_ID,
                        partyCard.getParty().getObjectId());
                startActivityForResult(intent, Constants.RequestCode.GET_PARTY_DETAILS_REQ_CODE);
            }
        });
        return card;
    }

    @Override
    protected List<Card> buildCards() throws Exception {
        List<Card> cards = new ArrayList<Card>();
        if (dataSet.size() > 0) {
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
            for (int i = 0; i < getData().size(); i++) {
                Card card = getData().get(i);
                Party item = ((PartyCard) card).getParty();
                if (item.getObjectId().equals(partyId)) {
                    Party party = partyStore.getPartyById(partyId);
                    getData().set(i, buildCard(party, currentUser.getObjectId()));
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
