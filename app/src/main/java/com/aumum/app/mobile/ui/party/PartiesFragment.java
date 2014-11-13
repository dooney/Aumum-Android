package com.aumum.app.mobile.ui.party;

import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

import com.aumum.app.mobile.core.dao.vm.UserVM;
import com.aumum.app.mobile.core.model.Party;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PartiesFragment extends PartyListFragment {

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) { }

    @Override
    protected List<Party> onGetUpwardsList(String after) throws Exception {
        UserVM currentUser = userStore.getCurrentUser(false);
        return dataStore.getUpwardsList(currentUser.getPartyList(), after);
    }

    @Override
    protected List<Party> onGetBackwardsList(String before) throws Exception {
        UserVM currentUser = userStore.getCurrentUser(false);
        return dataStore.getBackwardsList(currentUser.getPartyList(), before);
    }
}
