package com.aumum.app.mobile.ui.discovery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.events.ShowMomentActionsEvent;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 16/03/2015.
 */
public class DiscoveryFragment extends Fragment {

    @Inject Bus bus;

    @InjectView(R.id.tpi_header)protected DiscoveryTabPageIndicator indicator;
    @InjectView(R.id.vp_pages)protected ViewPager pager;

    private MenuItem momentMenu;
    private PagerAdapter pagerAdapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
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
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == DiscoveryTabPageIndicator.TAB_MOMENT) {
                    momentMenu.setVisible(true);
                } else {
                    momentMenu.setVisible(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        momentMenu = menu.add(Menu.NONE, 0, Menu.NONE, null);
        momentMenu.setActionView(R.layout.menuitem_more);
        momentMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View moreView = momentMenu.getActionView();
        ImageView moreIcon = (ImageView) moreView.findViewById(R.id.b_more);
        moreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bus.post(new ShowMomentActionsEvent());
            }
        });
        if (pager.getCurrentItem() != DiscoveryTabPageIndicator.TAB_MOMENT) {
            momentMenu.setVisible(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onDestroy();
        bus.unregister(this);
    }
}
