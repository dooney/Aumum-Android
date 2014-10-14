

package com.aumum.app.mobile.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aumum.app.mobile.R;

/**
 * Pager adapter
 */
public class BootstrapPagerAdapter extends FragmentPagerAdapter {

    private String pages[];

    public static final int PAGE_PARTY = 0;
    public static final int PAGE_MESSAGE = 1;
    public static final int PAGE_PROFILE = 2;

    /**
     * Create pager adapter
     *
     * @param resources
     * @param fragmentManager
     */
    public BootstrapPagerAdapter(final Resources resources, final FragmentManager fragmentManager) {
        super(fragmentManager);
        pages = resources.getStringArray(R.array.pages_array);
    }

    @Override
    public int getCount() {
        return pages.length;
    }

    @Override
    public Fragment getItem(final int position) {
        final Fragment result;
        switch (position) {
            case PAGE_PARTY:
                result = new PartyListFragment();
                break;
            case PAGE_MESSAGE:
                result = new MessageListFragment();
                break;
            case PAGE_PROFILE:
                result = new UserFragment();
                break;
            default:
                result = null;
                break;
        }
        if (result != null) {
            result.setArguments(new Bundle()); //TODO do we need this?
        }
        return result;
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return pages[position];
    }
}
