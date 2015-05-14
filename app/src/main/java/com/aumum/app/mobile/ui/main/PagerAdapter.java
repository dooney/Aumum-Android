package com.aumum.app.mobile.ui.main;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.conversation.ConversationFragment;
import com.aumum.app.mobile.ui.discovery.DiscoveryFragment;
import com.aumum.app.mobile.ui.message.MessageFragment;
import com.aumum.app.mobile.ui.moment.MomentFragment;
import com.aumum.app.mobile.ui.user.ProfileFragment;
import com.aumum.app.mobile.ui.view.tab.IconPagerAdapter;

/**
 * Pager adapter
 */
public class PagerAdapter extends FragmentPagerAdapter
    implements IconPagerAdapter{

    private String pages[];

    public static final int PAGE_HOME = 0;
    public static final int PAGE_CHAT = 1;
    public static final int PAGE_DISCOVERY = 2;
    public static final int PAGE_MESSAGE = 3;
    public static final int PAGE_PROFILE = 4;

    private int icons[] = {
            R.drawable.tab_home_icon,
            R.drawable.tab_chat_icon,
            R.drawable.tab_discovery_icon,
            R.drawable.tab_message_icon,
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
            case PAGE_HOME:
                result = new MomentFragment();
                break;
            case PAGE_CHAT:
                result = new ConversationFragment();
                break;
            case PAGE_DISCOVERY:
                result = new DiscoveryFragment();
                break;
            case PAGE_MESSAGE:
                result = new MessageFragment();
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
