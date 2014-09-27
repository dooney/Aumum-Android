package com.aumum.app.mobile.ui;

import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.widget.ListView;

import com.aumum.app.mobile.BootstrapServiceProvider;
import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.authenticator.LogoutService;
import com.aumum.app.mobile.core.Party;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PartyListFragment extends ItemListFragment<Party> {
    @Inject protected BootstrapServiceProvider serviceProvider;
    @Inject protected LogoutService logoutService;

    private final int NEW_PARTY_POST_REQ_CODE = 2031;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_parties);
    }

    @Override
    protected void configureList(final Activity activity, final ListView listView) {
        super.configureList(activity, listView);

        listView.setFastScrollEnabled(true);
        listView.setDividerHeight(0);
    }

    @Override
    protected LogoutService getLogoutService() {
        return logoutService;
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_parties;
    }

    @Override
    protected SingleTypeAdapter<Party> createAdapter(List<Party> items) {
        return new PartyListAdapter(getActivity().getLayoutInflater(), items);
    }

    @Override
    protected void newItem() {
        final Intent intent = new Intent(getActivity(), NewPartyPostActivity.class);
        startActivityForResult(intent, NEW_PARTY_POST_REQ_CODE);
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
    public Loader<List<Party>> onCreateLoader(int i, Bundle bundle) {
        final List<Party> initialItems = items;
        return new ThrowableLoader<List<Party>>(getActivity(), items) {
            @Override
            public List<Party> loadData() throws Exception {

                try {
                    List<Party> latest = null;

                    if (getActivity() != null) {
                        latest = serviceProvider.getService(getActivity()).getParties();
                    }

                    if (latest != null) {
                        return latest;
                    } else {
                        return Collections.emptyList();
                    }
                } catch (final OperationCanceledException e) {
                    final Activity activity = getActivity();
                    if (activity != null) {
                        activity.finish();
                    }
                    return initialItems;
                }
            }
        };
    }
}
