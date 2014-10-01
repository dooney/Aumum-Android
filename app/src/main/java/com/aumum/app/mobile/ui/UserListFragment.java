package com.aumum.app.mobile.ui;

import android.os.Bundle;

import com.aumum.app.mobile.BootstrapServiceProvider;
import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;

public class UserListFragment extends CardListFragment {

    @Inject protected BootstrapServiceProvider serviceProvider;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setHasOptionsMenu(false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_users);
    }

    @Override
    protected int getErrorMessage(final Exception exception) {
        return R.string.error_loading_users;
    }

    @Override
    protected List<Card> loadCards(int mode, String time) throws Exception {
        List<User> latest = null;

        if (getActivity() != null) {
            latest = serviceProvider.getService(getActivity()).getUsers();
        }

        if (latest != null) {
            return buildCards(latest);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected void handlePullToRefresh() {

    }

    @Override
    protected void handleLoadMoreRefresh() {

    }

    private List<Card> buildCards(List<User> items) {
        List<Card> cards = new ArrayList<Card>();
        for(User user: items) {
            Card card = new Card(getActivity());
            card.setTitle(user.getUsername());
            cards.add(card);
        }
        return cards;
    }
}
