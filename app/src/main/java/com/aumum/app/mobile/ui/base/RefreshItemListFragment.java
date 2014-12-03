package com.aumum.app.mobile.ui.base;

import android.accounts.OperationCanceledException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.aumum.app.mobile.R;

import java.util.List;

import it.gmariotti.cardslib.library.view.listener.SwipeOnScrollListener;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by Administrator on 28/09/2014.
 */
public abstract class RefreshItemListFragment<E> extends ItemListFragment<E> {

    private final String REFRESH_MODE = "refreshMode";
    protected final int UPWARDS_REFRESH = 1;
    protected final int BACKWARDS_REFRESH = 2;
    private boolean isLoading = false;
    private boolean loadMore = true;

    protected void setLoadMore(boolean loadMore) {
        this.loadMore = loadMore;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.card_list, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final PullToRefreshLayout pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable().listener(new OnRefreshListener() {
            @Override
            public void onRefreshStarted(View view) {
                if (!isLoading) {
                    doRefresh(UPWARDS_REFRESH);
                    pullToRefreshLayout.setRefreshComplete();
                }
            }
        }).setup(pullToRefreshLayout);

        getListView().setOnScrollListener(
                new SwipeOnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        super.onScrollStateChanged(view,scrollState);
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        int lastInScreen = firstVisibleItem + visibleItemCount;
                        if (visibleItemCount > 0 && lastInScreen == totalItemCount) {
                            if (!isLoading && loadMore) {
                                doRefresh(BACKWARDS_REFRESH);
                            }
                        }
                    }
                });
    }

    @Override
    protected List<E> loadDataCore(final Bundle bundle) throws Exception {
        try {
            int mode = UPWARDS_REFRESH;
            if (bundle != null) {
                mode = bundle.getInt(REFRESH_MODE);
            }
            isLoading = true;
            return loadByMode(mode);
        } catch (final OperationCanceledException e) {
            return getData();
        } finally {
            isLoading = false;
        }
    }

    protected void doRefresh(int mode) {
        final Bundle bundle = new Bundle();
        bundle.putInt(REFRESH_MODE, mode);
        refresh(bundle);
    }

    @Override
    protected void handleLoadResult(final List<E> result) {
        super.handleLoadResult(result);
        isLoading = false;
    }

    protected abstract List<E> loadByMode(int mode) throws Exception;
}
