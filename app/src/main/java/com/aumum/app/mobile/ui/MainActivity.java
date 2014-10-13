package com.aumum.app.mobile.ui;

import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.aumum.app.mobile.BootstrapServiceProvider;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.authenticator.ApiKeyProvider;
import com.aumum.app.mobile.authenticator.LogoutService;
import com.aumum.app.mobile.core.BootstrapService;
import com.aumum.app.mobile.events.SubscribeChannelEvent;
import com.aumum.app.mobile.events.UnSubscribeChannelEvent;
import com.aumum.app.mobile.util.SafeAsyncTask;

import javax.inject.Inject;

import butterknife.Views;


/**
 * Initial activity for the application.
 *
 * If you need to remove the authentication from the application please see
 * {@link com.aumum.app.mobile.authenticator.ApiKeyProvider#getAuthKey(android.app.Activity)}
 */
public class MainActivity extends BootstrapFragmentActivity {

    @Inject protected BootstrapServiceProvider serviceProvider;
    @Inject protected LogoutService logoutService;
    @Inject protected ApiKeyProvider apiKeyProvider;

    private String userChannel;

    private CarouselFragment carouselFragment;

    public static final String INTENT_NOTIFICATION = "intentNotification";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        // View injection with Butterknife
        Views.inject(this);

        checkAuth();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSubscribeUserChannel();
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
                unSubscribeUserChannel();
                checkAuth();
            }
        });
    }

    private void initScreen() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        carouselFragment = new CarouselFragment();
        Intent intent = getIntent();
        if (intent.hasExtra(INTENT_NOTIFICATION)) {
            carouselFragment.setLandingPage(BootstrapPagerAdapter.PAGE_MESSAGE);
            intent.removeExtra(INTENT_NOTIFICATION);
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, carouselFragment)
                .commit();
    }

    private void checkAuth() {
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                final BootstrapService svc = serviceProvider.getService(MainActivity.this);
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
                subscribeUserChannel();
                initScreen();
            }
        }.execute();
    }

    private void subscribeUserChannel() {
        userChannel = apiKeyProvider.getAuthUserId();
        if (userChannel != null) {
            eventBus.post(new SubscribeChannelEvent(userChannel));
        }
    }

    private void unSubscribeUserChannel() {
        if (userChannel != null) {
            eventBus.post(new UnSubscribeChannelEvent(userChannel));
        }
    }
}
