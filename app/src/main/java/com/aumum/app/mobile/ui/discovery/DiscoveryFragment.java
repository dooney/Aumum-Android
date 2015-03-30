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
import com.aumum.app.mobile.events.RefreshArticleEvent;
import com.aumum.app.mobile.events.RefreshChannelEvent;
import com.aumum.app.mobile.events.RefreshGameEvent;
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
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, null);
        menuItem.setActionView(R.layout.menuitem_refresh);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View refreshView = menuItem.getActionView();
        ImageView refreshIcon = (ImageView) refreshView.findViewById(R.id.b_refresh);
        refreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = pager.getCurrentItem();
                switch (position) {
                    case DiscoveryTabPageIndicator.TAB_CHANNEL:
                        bus.post(new RefreshChannelEvent());
                        break;
                    case DiscoveryTabPageIndicator.TAB_ARTICLE:
                        bus.post(new RefreshArticleEvent());
                        break;
                    case DiscoveryTabPageIndicator.TAB_GAME:
                        bus.post(new RefreshGameEvent());
                        break;
                    default:
                        break;
                }
            }
        });
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
