package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.GPSTracker;
import com.aumum.app.mobile.utils.Ln;

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
public class PartyListFragment extends RefreshItemListFragment<Card> {

    @Inject UserStore userStore;
    @Inject PartyStore dataStore;

    protected User currentUser;
    protected List<Party> dataSet = new ArrayList<Party>();

    protected GPSTracker gpsTracker;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
        gpsTracker = new GPSTracker(getActivity());
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_party_list, null);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.add(Menu.NONE, 0, Menu.NONE, "SEARCH")
                .setIcon(R.drawable.ic_fa_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(Menu.NONE, 1, Menu.NONE, "NEW")
                .setIcon(R.drawable.ic_fa_plus)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!isUsable()) {
            return false;
        }
        switch (item.getItemId()) {
            case 0:
                showSearchPartyDialog();
                break;
            case 1:
                final Intent intent = new Intent(getActivity(), NewPartyActivity.class);
                startActivityForResult(intent, Constants.RequestCode.NEW_PARTY_REQ_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RequestCode.NEW_PARTY_REQ_CODE && resultCode == Activity.RESULT_OK) {
            doRefresh(UPWARDS_REFRESH);
        } else if (requestCode == Constants.RequestCode.GET_PARTY_DETAILS_REQ_CODE && resultCode == Activity.RESULT_OK) {
            String partyId = data.getStringExtra(PartyDetailsActivity.INTENT_PARTY_ID);
            if (data.hasExtra(PartyDetailsActivity.INTENT_DELETED)) {
                onPartyDeleted(partyId);
            } else {
                onPartyRefresh(partyId);
            }
        } else if (requestCode == Constants.RequestCode.GET_PARTY_COMMENTS_REQ_CODE && resultCode == Activity.RESULT_OK) {
            String partyId = data.getStringExtra(PartyCommentsActivity.INTENT_PARTY_ID);
            onPartyRefresh(partyId);
        }
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_parties;
    }

    @Override
    protected List<Card> loadByMode(int mode) throws Exception {
        switch (mode) {
            case UPWARDS_REFRESH:
                getUpwardsList();
                break;
            case BACKWARDS_REFRESH:
                getBackwardsList();
                break;
            default:
                throw new Exception("Invalid refresh mode: " + mode);
        }
        return buildCards();
    }

    @Override
    protected ArrayAdapter<Card> createAdapter(List<Card> items) {
        return new CardArrayAdapter(getActivity(), items);
    }

    private void getUpwardsList() throws Exception {
        dataStore.getUnreadList().clear();
        String after = null;
        if (dataSet.size() > 0) {
            after = dataSet.get(0).getCreatedAt();
        }
        List<Party> partyList = onGetUpwardsList(after);
        Collections.reverse(partyList);
        for(Party party: partyList) {
            dataSet.add(0, party);
        }
    }

    private void getBackwardsList() throws Exception {
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
        Card card = new PartyCard(PartyListFragment.this, party, currentUserId);
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

    private List<Card> buildCards() throws Exception {
        currentUser = userStore.getCurrentUser();
        List<Card> cards = new ArrayList<Card>();
        if (dataSet.size() > 0) {
            for (Party party : dataSet) {
                Card card = buildCard(party, currentUser.getObjectId());
                cards.add(card);
            }
        }
        return cards;
    }

    private void showSearchPartyDialog() {
        final String options[] = getResources().getStringArray(R.array.label_search_party);
        DialogUtils.showDialog(getActivity(), options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
                intent.putExtra(SearchPartyActivity.INTENT_TITLE, options[i]);
                intent.putExtra(SearchPartyActivity.INTENT_NEARBY_PARTIES, true);
                startActivity(intent);
            }
        });
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
                    Party party = dataStore.getPartyById(partyId);
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
        return dataStore.getUpwardsList(after);
    }

    private List<Party> onGetBackwardsList(String before) throws Exception {
        return dataStore.getBackwardsList(before);
    }
}
