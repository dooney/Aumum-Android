package com.aumum.app.mobile.authenticator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aumum.app.mobile.authenticator.SplashFragment;

/**
 * Created by Simpson Du on 21/09/2014.
 */
public class SplashFragmentPagerAdapter extends FragmentPagerAdapter {

    public SplashFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int i) {
        return new SplashFragment();
    }

    @Override
    public int getCount() {
        return 4;
    }
}
