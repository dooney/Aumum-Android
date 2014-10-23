package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SearchViewCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.ui.base.CardListFragment;
import com.aumum.app.mobile.utils.Ln;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PartyListFragment extends CardListFragment
        implements Card.OnCardClickListener,
                   PartyActionListener.OnActionListener {

    private List<Party> dataSet = new ArrayList<Party>();

    private PartyStore dataStore;

    private UserStore userStore;

    private final int NEW_PARTY_POST_REQ_CODE = 30;
    private final int GET_PARTY_DETAILS_REQ_CODE = 31;

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
        View searchView = SearchViewCompat.newSearchView(getActivity());
        SearchViewCompat.setQueryHint(searchView, getString(R.string.hint_search_party));
        optionsMenu.add(Menu.NONE, 0, Menu.NONE, "Search")
                .setIcon(R.drawable.ic_fa_search)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        optionsMenu.add(Menu.NONE, 1, Menu.NONE, "NEW")
                .setIcon(R.drawable.ic_fa_plus)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!isUsable()) {
            return false;
        }
        if (item.getItemId() == 1) {
            final Intent intent = new Intent(getActivity(), PartyPostActivity.class);
            startActivityForResult(intent, NEW_PARTY_POST_REQ_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        dataStore.saveOfflineData(dataSet);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_PARTY_POST_REQ_CODE && resultCode == Activity.RESULT_OK) {
            doRefresh(UPWARDS_REFRESH);
        } else if (requestCode == GET_PARTY_DETAILS_REQ_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getBooleanExtra(PartyDetailsActivity.INTENT_PARTY_DELETED, false)) {
                String partyId = data.getStringExtra(PartyDetailsActivity.INTENT_PARTY_ID);
                if (partyId != null) {
                    onPartyDeletedSuccess(partyId);
                }
            }
        }
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_parties;
    }

    @Override
    protected List<Card> loadCards(int mode) throws Exception {
        List<Party> partyList;
        switch (mode) {
            case UPWARDS_REFRESH:
                partyList = getUpwardsList();
                break;
            case BACKWARDS_REFRESH:
                partyList = getBackwardsList();
                break;
            case STATIC_REFRESH:
                partyList = getStaticList();
                break;
            default:
                throw new Exception("Invalid refresh mode: " + mode);
        }
        if (partyList != null) {
            for (Party party : partyList) {
                User user = userStore.getUserById(party.getUserId(), false);
                party.setUser(user);
            }
        }
        return buildCards();
    }

    private List<Party> getUpwardsList() {
        dataStore.refresh(dataSet);
        String after = null;
        if (dataSet.size() > 0) {
            after = dataSet.get(0).getCreatedAt();
        }
        List<Party> partyList = dataStore.getUpwardsList(after);
        Collections.reverse(partyList);
        for(Party party: partyList) {
            dataSet.add(0, party);
        }
        return partyList;
    }

    private List<Party> getBackwardsList() {
        if (dataSet.size() > 0) {
            Party last = dataSet.get(dataSet.size() - 1);
            List<Party> partyList = dataStore.getBackwardsList(last.getCreatedAt());
            dataSet.addAll(partyList);
            if (partyList.size() > 0) {
                setLoadMore(true);
            } else {
                setLoadMore(false);
                Toaster.showShort(getActivity(), R.string.info_all_loaded);
            }
            return partyList;
        }
        return null;
    }

    private List<Party> getStaticList() {
        List<Party> partyList = dataStore.getOfflineList();
        dataSet.addAll(partyList);
        return partyList;
    }

    private List<Card> buildCards() {
        List<Card> cards = new ArrayList<Card>();
        if (dataSet.size() > 0) {
            User user = userStore.getCurrentUser(false);
            for (Party party : dataSet) {
                Card card = new PartyCard(getActivity(), party, user.getObjectId(), this, this);
                cards.add(card);
            }
        }
        return cards;
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
                    Toaster.showShort(getActivity(), R.string.info_party_deleted);
                    return;
                }
            }
        } catch (Exception e) {
            Ln.d(e);
        }
        Toaster.showLong(getActivity(), R.string.error_delete_party);
    }

    @Override
    public void onPartySharedSuccess() {

    }

    @Override
    public void onClick(Card card, View view) {
        PartyCard partyCard = (PartyCard) card;
        final Intent intent = new Intent(getActivity(), PartyDetailsActivity.class);
        intent.putExtra(PartyDetailsActivity.INTENT_PARTY_ID, partyCard.getParty().getObjectId());
        startActivityForResult(intent, GET_PARTY_DETAILS_REQ_CODE);
    }
}
