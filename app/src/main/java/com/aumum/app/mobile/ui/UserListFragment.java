package com.aumum.app.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.aumum.app.mobile.BootstrapServiceProvider;
import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;

import static com.aumum.app.mobile.core.Constants.Extra.USER;

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

    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        final User user = ((User) l.getItemAtPosition(position));

        startActivity(new Intent(getActivity(), UserActivity.class).putExtra(USER, user));
    }

    @Override
    protected int getErrorMessage(final Exception exception) {
        return R.string.error_loading_users;
    }

    @Override
    protected List<Card> loadCards() throws Exception {
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
