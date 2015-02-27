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
    private ArrayList<String> titles;
    private ArrayList<Integer> categories;
    private ArrayList<AskingListFragment> fragments;

    /**
     * Create pager adapter
     *
     * @param resources
     * @param fragmentManager
     */
    public PagerAdapter(final Resources resources, final FragmentManager fragmentManager) {
        super(fragmentManager);
        pages = resources.getStringArray(R.array.label_asking_pages);
        titles = new ArrayList<String>();
        categories = new ArrayList<Integer>();
        fragments = new ArrayList<AskingListFragment>();
        for(int i = 0; i < pages.length; i++) {
            String title = pages[i];
            if (title != null && title.length() > 0) {
                final AskingListFragment fragment = new AskingListFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(AskingListFragment.CATEGORY, i);
                bundle.putString(AskingListFragment.TITLE, title);
                fragment.setArguments(bundle);
                fragments.add(fragment);
                titles.add(title);
                categories.add(i);
            }
        }
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
        return titles.get(position);
    }

    public int getCategory(final int position) {
        return categories.get(position);
    }
}
