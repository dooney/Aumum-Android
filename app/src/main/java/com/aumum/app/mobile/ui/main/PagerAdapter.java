package com.aumum.app.mobile.ui.main;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.asking.AskingFragment;
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
    public static final int PAGE_ASKING = 1;
    public static final int PAGE_CONVERSATION = 2;
    public static final int PAGE_CONTACT = 3;
    public static final int PAGE_PROFILE = 4;

    /**
     * Create pager adapter
     *
     * @param resources
     * @param fragmentManager
     */
    public PagerAdapter(final Resources resources, final FragmentManager fragmentManager) {
        super(fragmentManager);
        pages = resources.getStringArray(R.array.label_main_pages);
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
            case PAGE_ASKING:
                result = new AskingFragment();
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
        return result;
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return pages[position];
    }
}
