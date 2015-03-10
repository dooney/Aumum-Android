package com.aumum.app.mobile.ui.base;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.etsy.android.grid.StaggeredGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 10/03/2015.
 */
public abstract class ItemGridFragment<E> extends LoaderFragment<List<E>> {

    private StaggeredGridView gridView;

    public StaggeredGridView getGridView() {
        return gridView;
    }

    public ItemGridFragment() {
        setData(new ArrayList<E>());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        gridView = null;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView = (StaggeredGridView ) view.findViewById(android.R.id.list);

        setGridAdapter(createAdapter(getData()));
    }

    /**
     * Create adapter to display items
     *
     * @param items
     * @return adapter
     */
    protected abstract ArrayAdapter<E> createAdapter(final List<E> items);

    protected ArrayAdapter<E> getGridAdapter() {
        return (ArrayAdapter<E>)gridView.getAdapter();
    }

    /**
     * Set list adapter to use on list view
     *
     * @param adapter
     * @return this fragment
     */
    protected ItemGridFragment<E> setGridAdapter(final ListAdapter adapter) {
        if (gridView != null) {
            gridView.setAdapter(adapter);
        }
        return this;
    }

    @Override
    protected boolean readyToShow() {
        return !getData().isEmpty();
    }

    @Override
    protected View getMainView() {
        return gridView;
    }

    @Override
    protected void handleLoadResult(List<E> result) {
        if (result != null) {
            getData().clear();
            getData().addAll(result);
            getGridAdapter().notifyDataSetChanged();
        }
    }
}
