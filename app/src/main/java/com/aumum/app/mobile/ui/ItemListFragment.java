
package com.aumum.app.mobile.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R.id;
import com.aumum.app.mobile.R.layout;
import com.github.kevinsawicki.wishlist.Toaster;
import com.github.kevinsawicki.wishlist.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


/**
 * Base fragment for displaying a list of items that loads with a progress bar
 * visible
 *
 * @param <E>
 */
public abstract class ItemListFragment<E> extends Fragment
        implements LoaderCallbacks<List<E>>, OnRefreshListener {

    private static final String FORCE_REFRESH = "forceRefresh";

    /**
     * @param args bundle passed to the loader by the LoaderManager
     * @return true if the bundle indicates a requested forced refresh of the
     * items
     */
    protected static boolean isForceRefresh(final Bundle args) {
        return args != null && args.getBoolean(FORCE_REFRESH, false);
    }

    /**
     * List items provided to {@link #onLoadFinished(Loader, List)}
     */
    protected List<E> items = new ArrayList<E>();

    /**
     * List view
     */
    protected ListView listView;

    /**
     * Empty view
     */
    protected TextView emptyView;

    /**
     * Progress bar
     */
    protected ProgressBar progressBar;

    /**
     * Is the list currently shown?
     */
    protected boolean listShown;

    private PullToRefreshLayout pullToRefreshLayout;

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!items.isEmpty()) {
            setListShown(true, false);
        }

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(layout.item_list, null);
    }

    /**
     * Detach from list view.
     */
    @Override
    public void onDestroyView() {
        listShown = false;
        emptyView = null;
        progressBar = null;
        listView = null;

        super.onDestroyView();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onListItemClick((ListView) parent, view, position, id);
            }
        });
        progressBar = (ProgressBar) view.findViewById(id.pb_loading);

        emptyView = (TextView) view.findViewById(android.R.id.empty);

        setListAdapter(createAdapter(items));

        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(id.ptr_layout);
        ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable().listener(this).setup(pullToRefreshLayout);
    }

    /**
     * Force a refresh of the items displayed ignoring any cached items
     */
    protected void forceRefresh() {
        final Bundle bundle = new Bundle();
        bundle.putBoolean(FORCE_REFRESH, true);
        refresh(bundle);
    }

    /**
     * Refresh the fragment's list
     */
    public void refresh() {
        refresh(null);
    }

    private void refresh(final Bundle args) {
        if (!isUsable()) {
            return;
        }

        getActionBarActivity().setSupportProgressBarIndeterminateVisibility(true);

        getLoaderManager().restartLoader(0, args, this);
    }

    private ActionBarActivity getActionBarActivity() {
        return ((ActionBarActivity) getActivity());
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    protected abstract int getErrorMessage(final Exception exception);

    public void onLoadFinished(final Loader<List<E>> loader, final List<E> items) {

        getActionBarActivity().setSupportProgressBarIndeterminateVisibility(false);

        final Exception exception = getException(loader);
        if (exception != null) {
            showError(getErrorMessage(exception));
            showList();
            return;
        }

        this.items = items;
        getListAdapter().addAll(this.items);
        showList();
    }

    /**
     * Create adapter to display items
     *
     * @param items
     * @return adapter
     */
    protected abstract ArrayAdapter<E> createAdapter(final List<E> items);

    /**
     * Set the list to be shown
     */
    protected void showList() {
        setListShown(true, isResumed());
    }

    @Override
    public void onLoaderReset(final Loader<List<E>> loader) {
        // Intentionally left blank
    }

    /**
     * Show exception in a Toast
     *
     * @param message
     */
    protected void showError(final int message) {
        Toaster.showLong(getActivity(), message);
    }

    /**
     * Get exception from loader if it provides one by being a
     * {@link ThrowableLoader}
     *
     * @param loader
     * @return exception or null if none provided
     */
    protected Exception getException(final Loader<List<E>> loader) {
        if (loader instanceof ThrowableLoader) {
            return ((ThrowableLoader<List<E>>) loader).clearException();
        } else {
            return null;
        }
    }

    /**
     * Refresh the list with the progress bar showing
     */
    protected void refreshWithProgress() {
        items.clear();
        setListShown(false);
        refresh();
    }

    /**
     * Get {@link ListView}
     *
     * @return listView
     */
    public ListView getListView() {
        return listView;
    }

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

    private ItemListFragment<E> fadeIn(final View view, final boolean animate) {
        if (view != null) {
            if (animate) {
                view.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        android.R.anim.fade_in));
            } else {
                view.clearAnimation();
            }
        }
        return this;
    }

    private ItemListFragment<E> show(final View view) {
        ViewUtils.setGone(view, false);
        return this;
    }

    private ItemListFragment<E> hide(final View view) {
        ViewUtils.setGone(view, true);
        return this;
    }

    /**
     * Set list shown or progress bar show
     *
     * @param shown
     * @return this fragment
     */
    public ItemListFragment<E> setListShown(final boolean shown) {
        return setListShown(shown, true);
    }

    /**
     * Set list shown or progress bar show
     *
     * @param shown
     * @param animate
     * @return this fragment
     */
    public ItemListFragment<E> setListShown(final boolean shown, final boolean animate) {
        if (!isUsable()) {
            return this;
        }

        if (shown == listShown) {
            if (shown) {
                // List has already been shown so hide/show the empty view with
                // no fade effect
                if (items.isEmpty()) {
                    hide(listView).show(emptyView);
                } else {
                    hide(emptyView).show(listView);
                }
            }
            return this;
        }

        listShown = shown;

        if (shown) {
            if (!items.isEmpty()) {
                hide(progressBar).hide(emptyView).fadeIn(listView, animate)
                        .show(listView);
            } else {
                hide(progressBar).hide(listView).fadeIn(emptyView, animate)
                        .show(emptyView);
            }
        } else {
            hide(listView).hide(emptyView).fadeIn(progressBar, animate)
                    .show(progressBar);
        }
        return this;
    }

    /**
     * Set empty text on list fragment
     *
     * @param message
     * @return this fragment
     */
    protected ItemListFragment<E> setEmptyText(final String message) {
        if (emptyView != null) {
            emptyView.setText(message);
        }
        return this;
    }

    /**
     * Set empty text on list fragment
     *
     * @param resId
     * @return this fragment
     */
    protected ItemListFragment<E> setEmptyText(final int resId) {
        if (emptyView != null) {
            emptyView.setText(resId);
        }
        return this;
    }

    /**
     * Callback when a list view item is clicked
     *
     * @param l
     * @param v
     * @param position
     * @param id
     */
    public void onListItemClick(final ListView l, final View v,
                                final int position, final long id) {
    }

    /**
     * Is this fragment still part of an activity and usable from the UI-thread?
     *
     * @return true if usable on the UI-thread, false otherwise
     */
    protected boolean isUsable() {
        return getActivity() != null;
    }

    @Override
    public void onRefreshStarted(View view) {
        forceRefresh();
        pullToRefreshLayout.setRefreshComplete();
    }
}
