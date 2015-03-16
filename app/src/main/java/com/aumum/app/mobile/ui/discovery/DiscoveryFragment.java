package com.aumum.app.mobile.ui.discovery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aumum.app.mobile.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 16/03/2015.
 */
public class DiscoveryFragment extends Fragment {

    @InjectView(R.id.tpi_header)protected DiscoveryTabPageIndicator indicator;
    @InjectView(R.id.vp_pages)protected ViewPager pager;

    private PagerAdapter pagerAdapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discovery, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ButterKnife.inject(this, getView());
        pagerAdapter = new PagerAdapter(getResources(), getChildFragmentManager());
        pager.setAdapter(pagerAdapter);
        indicator.setViewPager(pager);
    }
}
