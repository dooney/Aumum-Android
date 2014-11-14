package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.base.CardListFragment;
import com.aumum.app.mobile.utils.GPSTracker;
import com.aumum.app.mobile.utils.Ln;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PartyListFragment extends CardListFragment
        implements PartyActionListener.OnActionListener,
                   PartyDetailsListener {

    @Inject UserStore userStore;
    @Inject PartyStore dataStore;

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
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_parties);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        SubMenu search = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.hint_search_party));
        MenuItem item = search.getItem();
        item.setIcon(R.drawable.ic_fa_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

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
                showSearchPopupWindow(getActivity().findViewById(item.getItemId()));
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
    protected List<Card> loadCards(int mode) throws Exception {
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
        gpsTracker.getLocation();
        for (Party party: dataSet) {
            party.setDistance(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        }
        return buildCards();
    }

    private void getUpwardsList() throws Exception {
        dataStore.refresh(dataSet);
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
                setLoadMore(true);
            } else {
                setLoadMore(false);
                Toaster.showShort(getActivity(), R.string.info_all_loaded);
            }
        }
    }

    private List<Card> buildCards() throws Exception {
        List<Card> cards = new ArrayList<Card>();
        if (dataSet.size() > 0) {
            User currentUser = userStore.getCurrentUser();
            for (Party party : dataSet) {
                if (party.getUser() == null) {
                    party.setUser(userStore.getUserById(party.getUserId()));
                }
                Card card = new PartyCard(getActivity(), party, currentUser.getObjectId(), this, this);
                cards.add(card);
            }
        }
        return cards;
    }

    private void showSearchPopupWindow(View menuItemView) {
        PopupWindow popup = new PopupWindow(getActivity());
        View layout = getActivity().getLayoutInflater().inflate(R.layout.popup_window_search_party, null);
        popup.setContentView(layout);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(menuItemView, -menuItemView.getWidth() / 2, 0);

        TextView textSearchNearby = (TextView) layout.findViewById(R.id.text_search_nearby);
        textSearchNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
                startActivity(intent);
            }
        });

        TextView textSearchAddress = (TextView) layout.findViewById(R.id.text_search_address);
        textSearchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onPartyDeletedSuccess(String partyId) {
        try {
            List<Card> cardList = getData();
            for (Card card : cardList) {
                Party party = ((PartyCard) card).getParty();
                if (party.getObjectId().equals(partyId)) {
                    dataSet.remove(party);
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

    protected List<Party> onGetUpwardsList(String after) throws Exception {
        return dataStore.getUpwardsList(after);
    }

    protected List<Party> onGetBackwardsList(String before) throws Exception {
        return dataStore.getBackwardsList(before);
    }
}
