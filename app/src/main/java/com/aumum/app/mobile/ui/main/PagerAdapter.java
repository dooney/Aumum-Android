package com.aumum.app.mobile.ui.main;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.contact.ContactFragment;
import com.aumum.app.mobile.ui.conversation.ConversationFragment;
import com.aumum.app.mobile.ui.moment.MomentFragment;
import com.aumum.app.mobile.ui.user.ProfileFragment;
import com.aumum.app.mobile.ui.view.tab.IconPagerAdapter;

/**
 * Pager adapter
 */
public class PagerAdapter extends FragmentPagerAdapter
    implements IconPagerAdapter{

    private String pages[];

    public static final int PAGE_PHOTO = 0;
    public static final int PAGE_CHAT = 1;
    public static final int PAGE_CONTACT = 2;
    public static final int PAGE_PROFILE = 3;

    private int icons[] = {
            R.drawable.tab_photo_icon,
            R.drawable.tab_chat_icon,
            R.drawable.tab_contact_icon,
            R.drawable.tab_profile_icon
    };

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
    public int getIconResId(int index) {
        return icons[index];
    }

    @Override
    public int getCount() {
        return pages.length;
    }

    @Override
    public Fragment getItem(final int position) {
        final Fragment result;
        switch (position) {
            case PAGE_PHOTO:
                result = new MomentFragment();
                break;
            case PAGE_CHAT:
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
