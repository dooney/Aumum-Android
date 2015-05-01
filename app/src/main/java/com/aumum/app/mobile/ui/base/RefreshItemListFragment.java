package com.aumum.app.mobile.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.core.model.RefreshItem;
import com.aumum.app.mobile.ui.view.pulltorefresh.XListView;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 28/09/2014.
 */
public abstract class RefreshItemListFragment<E extends RefreshItem> extends Fragment
        implements XListView.IXListViewListener {

    private XListView xListView;
    private ArrayAdapter<E> adapter;
    private List<E> dataSet = new ArrayList<>();

    private SafeAsyncTask<Boolean> task;

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        xListView = (XListView) view.findViewById(android.R.id.list);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(true);
        xListView.setAutoLoadEnable(true);
        xListView.setXListViewListener(this);
        xListView.setRefreshTime(getTime());

        adapter = createAdapter(dataSet);
        xListView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String after = null;
                if (dataSet.size() > 0) {
                    after = dataSet.get(0).getCreatedAt();
                }
                List<E> result = refresh(after);
                dataSet.clear();
                dataSet.addAll(result);
                return true;
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    showError(e);
                }
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                onLoad();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
    }

    @Override
    public void onLoadMore() {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (dataSet.size() > 0) {
                    RefreshItem item = dataSet.get(dataSet.size() - 1);
                    List<E> result = loadMore(item.getCreatedAt());
                    dataSet.clear();
                    dataSet.addAll(result);
                }
                return true;
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    showError(e);
                }
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                onLoad();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }

    private void onLoad() {
        xListView.stopRefresh();
        xListView.stopLoadMore();
        xListView.setRefreshTime(getTime());
    }

    protected void showMsg(final String message) {
        if (message != null) {
            Toaster.showShort(getActivity(), message);
        }
    }

    protected void showError(final Exception e) {
        final Throwable cause = e.getCause() != null ? e.getCause() : e;
        if(cause != null) {
            Toaster.showShort(getActivity(), cause.getMessage());
        }
    }

    protected abstract ArrayAdapter<E> createAdapter(final List<E> items);

    protected abstract List<E> refresh(String after);

    protected abstract List<E> loadMore(String before);
}
