package com.aumum.app.mobile.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.ui.view.pulltorefresh.XListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 28/09/2014.
 */
public abstract class RefreshItemListFragment<E> extends Fragment
        implements XListView.IXListViewListener {

    private XListView xListView;
    private ArrayAdapter<E> adapter;
    private List<E> dataSet = new ArrayList<>();

    public List<E> getData() {
        return dataSet;
    }

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

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }

    protected abstract ArrayAdapter<E> createAdapter(final List<E> items);
}
