package com.aumum.app.mobile.ui.party;

import android.app.Activity;
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
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.Place;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.events.ResetPartyUnreadEvent;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.EditTextDialog;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.utils.GPSTracker;
import com.aumum.app.mobile.utils.GooglePlaceUtils;
import com.aumum.app.mobile.utils.Ln;
import com.github.kevinsawicki.wishlist.Toaster;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Arrays;
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
    @Inject PartyStore partyStore;
    @Inject Bus bus;

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
        MenuItem search = menu.add(Menu.NONE, 0, Menu.NONE, null);
        search.setActionView(R.layout.menuitem_search);
        search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View searchView = search.getActionView();
        ImageView searchIcon = (ImageView) searchView.findViewById(R.id.b_search);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchPartyDialog();
            }
        });

        MenuItem more = menu.add(Menu.NONE, 1, Menu.NONE, null);
        more.setActionView(R.layout.menuitem_more);
        more.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View moreView = more.getActionView();
        ImageView moreIcon = (ImageView) moreView.findViewById(R.id.b_more);
        moreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActionDialog();
            }
        });
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
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RequestCode.NEW_PARTY_REQ_CODE && resultCode == Activity.RESULT_OK) {
            refresh(null);
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
    protected String getErrorMessage(Exception exception) {
        return getString(R.string.error_load_parties);
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

    private void showSearchPartyDialog() {
        final String options[] = getResources().getStringArray(R.array.label_search_party);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                String title = options[i];
                switch (i) {
                    case 0:
                        startSearchPartyActivity(title);
                        break;
                    case 1:
                        showSearchAddressDialog(title);
                        break;
                    default:
                        break;
                }
            }
        }).show();
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

    private void startSearchPartyActivity(String title) {
        final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
        intent.putExtra(SearchPartyActivity.INTENT_TITLE, title);
        intent.putExtra(SearchPartyActivity.INTENT_NEARBY_PARTIES, true);
        startActivity(intent);
    }

    private void startSearchPartyActivity(String title, Place place) {
        final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
        intent.putExtra(SearchPartyActivity.INTENT_TITLE, title);
        intent.putExtra(SearchPartyActivity.INTENT_LOCATION_NEARBY_PARTIES, true);
        intent.putExtra(SearchPartyActivity.INTENT_LOCATION_LAT, place.getLatitude());
        intent.putExtra(SearchPartyActivity.INTENT_LOCATION_LNG, place.getLongitude());
        startActivity(intent);
    }

    private void showSearchAddressDialog(final String title) {
        EditTextDialog dialog = new EditTextDialog(getActivity(),
                R.layout.dialog_edit_text_multiline,
                R.string.hint_search_address,
                new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void call(Object value) throws Exception {
                        String address = (String) value;
                        final Place place = GooglePlaceUtils.getPlace(address);
                        if (place == null) {
                            throw new Exception(getString(R.string.error_invalid_party_address, address));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startSearchPartyActivity(title, place);
                            }
                        });
                    }

                    @Override
                    public void onException(String errorMessage) {
                        Toaster.showShort(getActivity(), errorMessage);
                    }

                    @Override
                    public void onSuccess(Object value) {
                    }
                });
        dialog.getValueText().setAdapter(new PlacesAutoCompleteAdapter(getActivity(),
                R.layout.place_autocomplete_listitem));
        dialog.show();
    }

    private void showActionDialog() {
        final String options[] = getResources().getStringArray(R.array.label_party_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                switch (i) {
                    case 0:
                        startNewPartyActivity();
                        break;
                    case 1:
                        startMyPartiesActivity(currentUser);
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    private void startNewPartyActivity() {
        final Intent intent = new Intent(getActivity(), NewPartyActivity.class);
        startActivityForResult(intent, Constants.RequestCode.NEW_PARTY_REQ_CODE);
    }

    private void startMyPartiesActivity(User user) {
        final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
        intent.putExtra(SearchPartyActivity.INTENT_TITLE, getString(R.string.label_my_parties));
        intent.putExtra(SearchPartyActivity.INTENT_USER_ID, user.getObjectId());
        startActivity(intent);
    }
}
