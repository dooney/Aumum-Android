package com.aumum.app.mobile.ui.recreation;

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
import com.aumum.app.mobile.events.RefreshGameEvent;
import com.aumum.app.mobile.events.RefreshGroupEvent;
import com.aumum.app.mobile.events.ShowConversationActionsEvent;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 26/03/2015.
 */
public class RecreationFragment extends Fragment {

    @Inject Bus bus;
    @InjectView(R.id.tpi_header)protected RecreationTabPageIndicator indicator;
    @InjectView(R.id.vp_pages)protected ViewPager pager;

    private MenuItem conversationMenu;
    private MenuItem groupMenu;
    private MenuItem gameMenu;
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
        return inflater.inflate(R.layout.fragment_recreation, null);
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
                toggleMenuItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        conversationMenu = menu.add(Menu.NONE, 0, Menu.NONE, null);
        conversationMenu.setActionView(R.layout.menuitem_more);
        conversationMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View moreView = conversationMenu.getActionView();
        ImageView moreIcon = (ImageView) moreView.findViewById(R.id.b_more);
        moreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bus.post(new ShowConversationActionsEvent());
            }
        });
        groupMenu = menu.add(Menu.NONE, 0, Menu.NONE, null);
        groupMenu.setActionView(R.layout.menuitem_refresh);
        groupMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View groupRefreshView = groupMenu.getActionView();
        ImageView groupRefreshIcon = (ImageView) groupRefreshView.findViewById(R.id.b_refresh);
        groupRefreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bus.post(new RefreshGroupEvent());
            }
        });
        gameMenu = menu.add(Menu.NONE, 0, Menu.NONE, null);
        gameMenu.setActionView(R.layout.menuitem_refresh);
        gameMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View gameRefreshView = gameMenu.getActionView();
        ImageView gameRefreshIcon = (ImageView) gameRefreshView.findViewById(R.id.b_refresh);
        gameRefreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bus.post(new RefreshGameEvent());
            }
        });
        toggleMenuItem(pager.getCurrentItem());
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

    private void toggleMenuItem(int position) {
        conversationMenu.setVisible(false);
        groupMenu.setVisible(false);
        gameMenu.setVisible(false);
        switch (position) {
            case RecreationTabPageIndicator.TAB_CONVERSATION:
                conversationMenu.setVisible(true);
                break;
            case RecreationTabPageIndicator.TAB_GROUP:
                groupMenu.setVisible(true);
                break;
            case RecreationTabPageIndicator.TAB_GAME:
                gameMenu.setVisible(true);
                break;
            default:
                break;
        }
    }
}
