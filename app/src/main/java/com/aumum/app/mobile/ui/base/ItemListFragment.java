
package com.aumum.app.mobile.ui.base;

import android.os.Bundle;
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

    private ListView listView;

    public ListView getListView() {
        return listView;
    }

    public ItemListFragment() {
        setData(new ArrayList<E>());
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setHorizontalScrollBarEnabled(false);
        listView.setVerticalScrollBarEnabled(false);

        setListAdapter(createAdapter(getData()));
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
        return !getData().isEmpty();
    }

    @Override
    protected View getMainView() {
        return listView;
    }

    @Override
    protected void handleLoadResult(List<E> result) {
        if (result != null) {
            getData().clear();
            getData().addAll(result);
            getListAdapter().notifyDataSetChanged();
        }
    }
}
