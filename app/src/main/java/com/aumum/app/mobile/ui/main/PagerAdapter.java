

package com.aumum.app.mobile.ui.main;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.conversation.ConversationFragment;
import com.aumum.app.mobile.ui.contact.ContactFragment;
import com.aumum.app.mobile.ui.party.PartyListFragment;
import com.aumum.app.mobile.ui.user.ProfileFragment;

/**
 * Pager adapter
 */
public class PagerAdapter extends FragmentPagerAdapter {

    private String pages[];

    public static final int PAGE_PARTY = 0;
    public static final int PAGE_CONVERSATION = 1;
    public static final int PAGE_CONTACT = 2;
    public static final int PAGE_PROFILE = 3;

    /**
     * Create pager adapter
     *
     * @param resources
     * @param fragmentManager
     */
    public PagerAdapter(final Resources resources, final FragmentManager fragmentManager) {
        super(fragmentManager);
        pages = resources.getStringArray(R.array.label_pages);
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
            case PAGE_CONVERSATION:
                result = new ConversationFragment();
                break;
            case PAGE_CONTACT:
                result = new ContactFragment();
                break;
            case PAGE_PROFILE:
                result = new ProfileFragment();
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
