package com.aumum.app.mobile.ui;

import android.content.Intent;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.authenticator.BootstrapAuthenticatorActivity;
import com.aumum.app.mobile.core.Constants;
import com.viewpagerindicator.CirclePageIndicator;

public class SplashActivity extends ActionBarActivity {
    private FragmentPagerAdapter mAdapter;
    private ViewPager mPager;
    private CirclePageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAdapter = new SplashFragmentPagerAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }

    public void showLogin(final View view) {
        final Intent loginIntent = new Intent(this, BootstrapAuthenticatorActivity.class);
        startActivity(loginIntent);
    }
}
