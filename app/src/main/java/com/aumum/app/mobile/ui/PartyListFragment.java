package com.aumum.app.mobile.ui;

import android.accounts.OperationCanceledException;
import android.app.Activity;
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
    @Inject
    protected BootstrapServiceProvider serviceProvider;
    @Inject protected LogoutService logoutService;


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
