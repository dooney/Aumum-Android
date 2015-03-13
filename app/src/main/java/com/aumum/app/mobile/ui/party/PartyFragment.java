package com.aumum.app.mobile.ui.party;

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
 * Created by Administrator on 13/03/2015.
 */
public class PartyFragment extends Fragment {

    @InjectView(R.id.tpi_header)protected PartyTabPageIndicator indicator;
    @InjectView(R.id.vp_pages)protected ViewPager pager;

    private PagerAdapter pagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_party, null);
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
