package com.aumum.app.mobile.ui.base;

import android.accounts.OperationCanceledException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

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
    private final int UPWARDS_REFRESH = 1;
    private final int BACKWARDS_REFRESH = 2;
    private boolean isLoading = false;
    private boolean isMore = true;

    private PullToRefreshLayout pullToRefreshLayout;
    private View noMoreLayout;
    private View loadingLayout;

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
        if (pullToRefreshLayout != null) {
            ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable().listener(new OnRefreshListener() {
                @Override
                public void onRefreshStarted(View view) {
                    if (!isLoading) {
                        final Bundle bundle = new Bundle();
                        bundle.putInt(REFRESH_MODE, UPWARDS_REFRESH);
                        refresh(bundle);
                    }
                }
            }).setup(pullToRefreshLayout);
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        View footerView = inflater.inflate(R.layout.listview_footer, null);
        listView.addFooterView(footerView, null, false);
        listView.setFooterDividersEnabled(false);
        noMoreLayout = footerView.findViewById(R.id.layout_no_more);
        loadingLayout = footerView.findViewById(R.id.layout_loading);
        toggleLoadingView();

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
                                final Bundle bundle = new Bundle();
                                bundle.putInt(REFRESH_MODE, BACKWARDS_REFRESH);
                                refresh(bundle);
                            }
                        }
                    }
                });

        super.onViewCreated(view, savedInstanceState);
        if (emptyText != null) {
            emptyText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reload();
                }
            });
        }
    }

    @Override
    protected ArrayAdapter<E> getListAdapter() {
        HeaderViewListAdapter adapter = (HeaderViewListAdapter)getListView().getAdapter();
        return (ArrayAdapter<E>)adapter.getWrappedAdapter();
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pullToRefreshLayout != null) {
                        pullToRefreshLayout.setRefreshComplete();
                    }
                }
            });
        }
    }

    private void toggleLoadingView() {
        if (isMore) {
            noMoreLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
        } else {
            loadingLayout.setVisibility(View.GONE);
            noMoreLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setMore(final boolean isMore) {
        this.isMore = isMore;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toggleLoadingView();
            }
        });
    }

    private List<E> loadByMode(int mode) throws Exception {
        switch (mode) {
            case UPWARDS_REFRESH:
                getUpwardsList();
                break;
            case BACKWARDS_REFRESH:
                getBackwardsList();
                break;
            default:
                throw new Exception("Invalid refresh mode: " + mode);
        }
        return buildCards();
    }

    protected abstract void getUpwardsList() throws Exception;

    protected abstract void getBackwardsList() throws Exception;

    protected abstract List<E> buildCards() throws Exception;
}
