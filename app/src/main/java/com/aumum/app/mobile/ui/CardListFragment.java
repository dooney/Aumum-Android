package com.aumum.app.mobile.ui;

import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

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
    protected final int STATIC_REFRESH = 3;
    private int currentRefreshMode;
    private boolean isLoading = false;
    private boolean isMore = true;

    private PullToRefreshLayout pullToRefreshLayout;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.card_list, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable().listener(new OnRefreshListener() {
            @Override
            public void onRefreshStarted(View view) {
                if (!isLoading) {
                    doRefresh(UPWARDS_REFRESH, null);
                    pullToRefreshLayout.setRefreshComplete();
                }
            }
        }).setup(pullToRefreshLayout);

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
                                String time = getLastItemTime();
                                if (time != null) {
                                    doRefresh(BACKWARDS_REFRESH, time);
                                }
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
    protected List<Card> loadDataCore(final Bundle bundle) throws Exception {
        try {
            int mode = STATIC_REFRESH;
            String time = null;
            if (bundle != null) {
                mode = bundle.getInt(REFRESH_MODE);
                time = bundle.getString(TIME_BEFORE);
            }
            if (mode == STATIC_REFRESH && !hasOfflineData()) {
                mode = UPWARDS_REFRESH;
            }
            currentRefreshMode = mode;
            isLoading = true;
            return loadCards(mode, time);
        } catch (final OperationCanceledException e) {
            return data;
        } finally {
            isLoading = false;
        }
    }

    private void doRefresh(int mode, String time) {
        final Bundle bundle = new Bundle();
        bundle.putInt(REFRESH_MODE, mode);
        bundle.putString(TIME_BEFORE, time);
        refresh(bundle);
    }

    protected void handleLoadResult(final List<Card> result) {
        switch (currentRefreshMode) {
            case UPWARDS_REFRESH:
                for(Card item: result) {
                    data.add(0, item);
                }
                break;
            case BACKWARDS_REFRESH:
            case STATIC_REFRESH:
                data.addAll(result);
                isMore = result.size() > 0;
                break;
            default:
                break;
        }
        getListAdapter().notifyDataSetChanged();
        isLoading = false;
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            doRefresh(UPWARDS_REFRESH, null);
        }
    }

    protected abstract String getLastItemTime();

    protected abstract boolean hasOfflineData();

    protected abstract List<Card> loadCards(int mode, String time) throws Exception;
}
