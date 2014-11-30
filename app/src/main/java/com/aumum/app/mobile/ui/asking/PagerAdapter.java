package com.aumum.app.mobile.ui.asking;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 25/11/2014.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    private String pages[];

    /**
     * Create pager adapter
     *
     * @param resources
     * @param fragmentManager
     */
    public PagerAdapter(final Resources resources, final FragmentManager fragmentManager) {
        super(fragmentManager);
        pages = resources.getStringArray(R.array.label_asking_pages);
    }

    @Override
    public int getCount() {
        return pages.length;
    }

    @Override
    public Fragment getItem(final int position) {
        final Fragment result = new AskingListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(AskingListFragment.CATEGORY, position);
        bundle.putString(AskingListFragment.TITLE, pages[position]);
        result.setArguments(bundle);
        return result;
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return pages[position];
    }
}
