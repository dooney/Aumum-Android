package com.aumum.app.mobile.ui.moment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.CardListFragment;
import com.aumum.app.mobile.ui.party.PartiesActivity;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MomentsFragment extends CardListFragment {

    protected MenuItem checkInButton;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.label_check_in))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        checkInButton = menu.findItem(0);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!isUsable()) {
            return false;
        }
        final Intent intent = new Intent(getActivity(), PartiesActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_moments;
    }

    @Override
    protected List<Card> loadCards(int mode) throws Exception {
        return new ArrayList<Card>();
    }
}
