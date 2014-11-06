package com.aumum.app.mobile.ui.party;

import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.User;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PartiesFragment extends PartyListFragment {

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) { }

    @Override
    protected List<Party> onGetUpwardsList(String after) {
        User currentUser = userStore.getCurrentUser(false);
        return dataStore.getUpwardsList(currentUser.getParties(), after);
    }

    @Override
    protected List<Party> onGetBackwardsList(String before) {
        User currentUser = userStore.getCurrentUser(false);
        return dataStore.getBackwardsList(currentUser.getParties(), before);
    }
}
