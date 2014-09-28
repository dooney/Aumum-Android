package com.aumum.app.mobile.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.aumum.app.mobile.BootstrapServiceProvider;
import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Party;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PartyListFragment extends CardListFragment {
    @Inject protected BootstrapServiceProvider serviceProvider;

    private final int NEW_PARTY_POST_REQ_CODE = 31;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setHasOptionsMenu(true);
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
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEW_PARTY_POST_REQ_CODE:
                onNewPartyPostResult(resultCode, data);
                break;
            default:
                break;
        }
        return;
    }

    private void onNewPartyPostResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            forceRefresh();
        }
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_parties;
    }

    @Override
    protected List<Card> loadCards() throws Exception {
        List<Party> latest = null;

        if (getActivity() != null) {
            latest = serviceProvider.getService(getActivity()).getParties();
        }

        if (latest != null) {
            return buildCards(latest);
        } else {
            return Collections.emptyList();
        }
    }

    private List<Card> buildCards(List<Party> items) {
        List<Card> cards = new ArrayList<Card>();
        for(Party party: items) {
            Card card = new Card(getActivity());
            card.setTitle(party.getTitle());
            cards.add(card);
        }
        return cards;
    }
}
