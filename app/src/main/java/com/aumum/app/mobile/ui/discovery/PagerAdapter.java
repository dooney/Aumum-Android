package com.aumum.app.mobile.ui.discovery;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.feed.article.ArticleListFragment;
import com.aumum.app.mobile.ui.feed.channel.ChannelListFragment;
import com.aumum.app.mobile.ui.moment.MomentListFragment;

import java.util.ArrayList;

/**
 * Created by Administrator on 16/03/2015.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    private String pages[];
    private ArrayList<Fragment> fragments;

    /**
     * Create pager adapter
     *
     * @param resources
     * @param fragmentManager
     */
    public PagerAdapter(final Resources resources, final FragmentManager fragmentManager) {
        super(fragmentManager);
        pages = resources.getStringArray(R.array.label_discovery_pages);
        fragments = new ArrayList<Fragment>();
        fragments.add(new ChannelListFragment());
        fragments.add(new ArticleListFragment());
        fragments.add(new MomentListFragment());
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(final int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return pages[position];
    }
}
