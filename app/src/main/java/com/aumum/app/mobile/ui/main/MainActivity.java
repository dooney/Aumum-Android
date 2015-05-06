package com.aumum.app.mobile.ui.main;

import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.ServiceProvider;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.LogoutEvent;
import com.aumum.app.mobile.ui.base.BaseFragmentActivity;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.ShareUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class MainActivity extends BaseFragmentActivity {
    @Inject ServiceProvider serviceProvider;
    @Inject Bus bus;

    private Fragment mainFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_main);
        initScreen();
        ShareUtils.init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCode.SETTINGS_REQ_CODE
                && resultCode == RESULT_OK) {
            if (data.getBooleanExtra("logout", false)) {
                doLogout();
            }
        }
        ShareUtils.registerSSOCallback(requestCode, resultCode, data);
    }

    private void initScreen() {
        mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mainFragment)
                .commit();
    }

    private void resetScreen() {
        getSupportFragmentManager().beginTransaction()
                .remove(mainFragment)
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

    private void doLogout() {
        checkAuth();
        resetScreen();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Subscribe
    public void onLogoutEvent(LogoutEvent event) {
        doLogout();
    }
}
