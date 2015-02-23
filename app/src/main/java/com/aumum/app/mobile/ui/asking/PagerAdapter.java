package com.aumum.app.mobile.ui.asking;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aumum.app.mobile.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 25/11/2014.
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
        pages = resources.getStringArray(R.array.label_asking_pages);
        fragments = new ArrayList<Fragment>();
        for(int i = 0; i < pages.length; i++) {
            final Fragment fragment = new AskingListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(AskingListFragment.CATEGORY, i);
            bundle.putString(AskingListFragment.TITLE, pages[i]);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
    }

    @Override
    public int getCount() {
        return pages.length;
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
