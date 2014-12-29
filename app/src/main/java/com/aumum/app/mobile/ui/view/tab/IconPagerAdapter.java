package com.aumum.app.mobile.ui.view.tab;

/**
 * Created by Administrator on 29/12/2014.
 */
public interface IconPagerAdapter {
    /**
     * Get icon representing the page at {@code index} in the adapter.
     */
    int getIconResId(int index);

    // From PagerAdapter
    int getCount();
}
