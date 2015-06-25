package com.aumum.app.mobile.ui.view.paginggrid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import com.aumum.app.mobile.R;

import java.util.List;

/**
 * Created by Administrator on 20/06/2015.
 */
public class PagingGridView extends HeaderFooterGridView {

    private boolean isLoading;
    private boolean hasMoreItems;
    private Paging pagingListener;
    private View loadingView;

    public PagingGridView(Context context) {
        super(context);
        init();
    }

    public PagingGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PagingGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setPagingListener(Paging listener) {
        this.pagingListener = listener;
    }

    public void setHasMoreItems(boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
        if(!this.hasMoreItems) {
            this.removeFooterView(loadingView);
        }

    }

    public void onFinishLoading(boolean hasMoreItems, List<? extends Object> newItems) {
        setHasMoreItems(hasMoreItems);
        isLoading = false;
        if (newItems != null && newItems.size() > 0) {
            ListAdapter adapter = ((HeaderFooterViewGridAdapter)this.getAdapter()).getWrappedAdapter();
            if(adapter instanceof PagingBaseAdapter) {
                ((PagingBaseAdapter)adapter).addMoreItems(newItems);
            }
        }

    }

    private void init() {
        isLoading = false;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        loadingView = inflater.inflate(R.layout.grid_footer, null);
        addFooterView(loadingView);
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (!isLoading && hasMoreItems && getLastVisiblePosition() == getCount() - 1 && pagingListener != null) {
                        isLoading = false;
                        pagingListener.onLoadMoreItems();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view,
                                 int firstVisibleItem,
                                 int visibleItemCount,
                                 int totalItemCount) {

            }
        });
    }

    public interface Paging {
        void onLoadMoreItems();
    }
}
