package com.aumum.app.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.PartyStore;
import com.aumum.app.mobile.core.Party;
import com.aumum.app.mobile.core.User;
import com.aumum.app.mobile.core.UserStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PartyListFragment extends CardListFragment {

    private List<Party> dataSet = new ArrayList<Party>();

    private PartyStore dataStore;

    private UserStore userStore;

    private final int NEW_PARTY_POST_REQ_CODE = 31;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        userStore = UserStore.getInstance(getActivity());
        dataStore = new PartyStore(getActivity());
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_parties);
    }

    @Override
    public void onCreateOptionsMenu(final Menu optionsMenu, final MenuInflater inflater) {
        inflater.inflate(R.menu.page_party, optionsMenu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!isUsable()) {
            return false;
        }
        switch (item.getItemId()) {
            case R.id.b_new_party:
                final Intent intent = new Intent(getActivity(), NewPartyPostActivity.class);
                startActivityForResult(intent, NEW_PARTY_POST_REQ_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        dataStore.saveOfflineData(dataSet);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_parties;
    }

    @Override
    protected String getLastItemTime() {
        if (dataSet.size() > 0) {
            Party last = dataSet.get(dataSet.size() - 1);
            return last.getCreatedAt();
        }
        return null;
    }

    @Override
    protected boolean hasOfflineData() {
        return dataStore.hasOfflineData();
    }

    @Override
    protected List<Card> loadCards(int mode, String time) throws Exception {
        List<Party> partyList;
        switch (mode) {
            case UPWARDS_REFRESH:
                partyList = dataStore.getUpwardsList();
                Collections.reverse(partyList);
                for(Party party: partyList) {
                    dataSet.add(0, party);
                }
                break;
            case BACKWARDS_REFRESH:
                partyList = dataStore.getBackwardsList(time);
                dataSet.addAll(partyList);
                break;
            case STATIC_REFRESH:
                partyList = dataStore.getOfflineList();
                dataSet.addAll(partyList);
                break;
            default:
                throw new Exception("Invalid refresh mode: " + mode);
        }
        if (partyList != null) {
            for(Party party: partyList) {
                User user = userStore.getUserById(party.getUserId());
                party.setUser(user);
            }
            return buildCards(partyList);
        }
        return new ArrayList<Card>();
    }

    private List<Card> buildCards(List<Party> partyList) {
        List<Card> cards = new ArrayList<Card>();
        for(Party party: partyList) {
            Card card = new PartyCard(getActivity(), party);
            cards.add(card);
        }
        return cards;
    }
}
