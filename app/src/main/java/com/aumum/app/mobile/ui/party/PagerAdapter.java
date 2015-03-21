package com.aumum.app.mobile.ui.party;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.vendor.EventCategoryListFragment;

import java.util.ArrayList;

/**
 * Created by Administrator on 13/03/2015.
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
        pages = resources.getStringArray(R.array.label_party_pages);
        fragments = new ArrayList<Fragment>();
        fragments.add(new PartyRequestListFragment());
        fragments.add(new PartyListFragment());
        fragments.add(new EventCategoryListFragment());
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
