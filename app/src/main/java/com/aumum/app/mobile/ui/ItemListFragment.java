
package com.aumum.app.mobile.ui;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Base fragment for displaying a list of items that loads with a progress bar
 * visible
 *
 * @param <E>
 */
public abstract class ItemListFragment<E> extends LoaderFragment<List<E>> {

    protected ListView listView;

    public ItemListFragment() {
        data = new ArrayList<E>();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        listView = null;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(android.R.id.list);

        setListAdapter(createAdapter(data));
    }

    protected void refresh(final Bundle args) {
        if (!isUsable()) {
            return;
        }

        getActionBarActivity().setSupportProgressBarIndeterminateVisibility(true);

        getLoaderManager().restartLoader(0, args, this);
    }

    private ActionBarActivity getActionBarActivity() {
        return ((ActionBarActivity) getActivity());
    }

    public void onLoadFinished(final Loader<List<E>> loader, final List<E> items) {
        getActionBarActivity().setSupportProgressBarIndeterminateVisibility(false);

        super.onLoadFinished(loader, items);
    }

    /**
     * Create adapter to display items
     *
     * @param items
     * @return adapter
     */
    protected abstract ArrayAdapter<E> createAdapter(final List<E> items);

    protected ArrayAdapter<E> getListAdapter() {
        return (ArrayAdapter<E>)listView.getAdapter();
    }

    /**
     * Set list adapter to use on list view
     *
     * @param adapter
     * @return this fragment
     */
    protected ItemListFragment<E> setListAdapter(final ListAdapter adapter) {
        if (listView != null) {
            listView.setAdapter(adapter);
        }
        return this;
    }

    @Override
    protected boolean readyToShow() {
        return !data.isEmpty();
    }

    @Override
    protected View getMainView() {
        return listView;
    }
}
