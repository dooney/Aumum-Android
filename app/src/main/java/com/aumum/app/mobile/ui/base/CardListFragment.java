package com.aumum.app.mobile.ui.base;

import android.accounts.OperationCanceledException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.NetworkUtils;
import com.github.kevinsawicki.wishlist.Toaster;

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
                            if (!isLoading && isMore) {
                                doRefresh(BACKWARDS_REFRESH);
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
            int mode = UPWARDS_REFRESH;
            if (!NetworkUtils.isNetworkAvailable(getActivity())) {
                Toaster.showLong(getActivity(), R.string.message_loading_offline_data);
                mode = STATIC_REFRESH;
            } else if (bundle != null) {
                mode = bundle.getInt(REFRESH_MODE);
            }
            currentRefreshMode = mode;
            isLoading = true;
            return loadCards(mode);
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
    protected void handleLoadResult(final List<Card> result) {
        try {
            if (result != null) {
                switch (currentRefreshMode) {
                    case UPWARDS_REFRESH:
                        for (Card item : result) {
                            if (!getData().contains(item)) {
                                getData().add(0, item);
                            }
                        }
                        break;
                    case BACKWARDS_REFRESH:
                    case STATIC_REFRESH:
                        getData().addAll(result);
                        isMore = result.size() > 0;
                        break;
                    default:
                        break;
                }
                getListAdapter().notifyDataSetChanged();
            }
            isLoading = false;
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    protected abstract List<Card> loadCards(int mode) throws Exception;
}
