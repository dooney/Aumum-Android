package com.aumum.app.mobile.ui.main;

import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.ServiceProvider;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.LogoutService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.BaseFragmentActivity;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

public class MainActivity extends BaseFragmentActivity {
    @Inject protected ServiceProvider serviceProvider;
    @Inject protected LogoutService logoutService;
    @Inject protected ChatService chatService;

    private MainFragment mainFragment;

    public static final String INTENT_NOTIFICATION = "intentNotification";

    private boolean doubleBackToExitPressedOnce;
    private static final int DOUBLE_BACK_DELAY = 2000;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_main);
        initScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bootstrap, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        logoutService.logout(new Runnable() {
            @Override
            public void run() {
                chatService.logOut();
                checkAuth();
            }
        });
    }

    private void initScreen() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        mainFragment = new MainFragment();
        Intent intent = getIntent();
        if (intent.hasExtra(INTENT_NOTIFICATION)) {
            mainFragment.setLandingPage(PagerAdapter.PAGE_MESSAGE);
            intent.removeExtra(INTENT_NOTIFICATION);
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainFragment)
                .commit();
    }

    private void checkAuth() {
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                final RestService svc = serviceProvider.getService(MainActivity.this);
                return svc != null;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
                    finish();
                }
            }

            @Override
            protected void onSuccess(final Boolean hasAuthenticated) throws Exception {
                super.onSuccess(hasAuthenticated);
                initScreen();
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toaster.showShort(this, R.string.info_click_back_again);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, DOUBLE_BACK_DELAY);
    }
}
