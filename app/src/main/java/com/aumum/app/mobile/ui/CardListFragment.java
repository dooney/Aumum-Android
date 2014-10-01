package com.aumum.app.mobile.ui;

import android.accounts.OperationCanceledException;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aumum.app.mobile.R;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.listener.SwipeOnScrollListener;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by Administrator on 28/09/2014.
 */
public abstract class CardListFragment extends ItemListFragment<Card> {

    private final String REFRESH_MODE = "refreshMode";
    private final String TIME_BEFORE = "timeBefore";
    protected final int UPWARDS_REFRESH = 1;
    protected final int BACKWARDS_REFRESH = 2;
    private int currentRefreshMode;
    private boolean isLoading = false;
    private boolean isMore = true;

    private PullToRefreshLayout pullToRefreshLayout;

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable().listener(new OnRefreshListener() {
            @Override
            public void onRefreshStarted(View view) {
                if (!isLoading) {
                    handlePullToRefresh();
                    pullToRefreshLayout.setRefreshComplete();
                }
            }
        }).setup(pullToRefreshLayout);

        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setOnScrollListener(
                new SwipeOnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        super.onScrollStateChanged(view,scrollState);
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        int lastInScreen = firstVisibleItem + visibleItemCount;
                        if (visibleItemCount > 0 && lastInScreen == totalItemCount) {
                            if (!isLoading && isMore) {
                                handleLoadMoreRefresh();
                            }
                        }
                    }
                });
    }

    @Override
    protected ArrayAdapter<Card> createAdapter(List<Card> items) {
        return new CardArrayAdapter(getActivity(), items);
    }

    @Override
    public Loader<List<Card>> onCreateLoader(int i, final Bundle bundle) {
        final List<Card> initialItems = items;
        return new ThrowableLoader<List<Card>>(getActivity(), items) {
            @Override
            public List<Card> loadData() throws Exception {
                try {
                    int mode = UPWARDS_REFRESH;
                    String time = null;
                    if (bundle != null) {
                        mode = bundle.getInt(REFRESH_MODE);
                        time = bundle.getString(TIME_BEFORE);
                    }
                    currentRefreshMode = mode;
                    isLoading = true;
                    return loadCards(mode, time);
                } catch (final OperationCanceledException e) {
                    isLoading = false;
                    return initialItems;
                }
            }
        };
    }

    protected abstract List<Card> loadCards(int mode, String time) throws Exception;

    protected void doRefresh(int mode, String time) {
        final Bundle bundle = new Bundle();
        bundle.putInt(REFRESH_MODE, mode);
        bundle.putString(TIME_BEFORE, time);
        refresh(bundle);
    }

    protected void handleLoadResult(final List<Card> result) {
        switch (currentRefreshMode) {
            case UPWARDS_REFRESH:
                for(Card item: result) {
                    items.add(0, item);
                }
                break;
            case BACKWARDS_REFRESH:
                items.addAll(result);
                isMore = result.size() > 0;
                break;
            default:
                break;
        }
        getListAdapter().notifyDataSetChanged();
        isLoading = false;
    }

    protected abstract void handlePullToRefresh();

    protected abstract void handleLoadMoreRefresh();
}
