package com.aumum.app.mobile.ui.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.aumum.app.mobile.R;
import com.github.kevinsawicki.wishlist.Toaster;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 21/05/2015.
 */
public class BaseActionBarActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.actionbar_back_button);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    protected void showMsg(final int messageId) {
        if (messageId > 0) {
            Toaster.showShort(this, getString(messageId));
        }
    }

    protected void showMsg(final String message) {
        if (message != null) {
            Toaster.showShort(this, message);
        }
    }

    protected void showError(final Exception e) {
        final Throwable cause = e.getCause() != null ? e.getCause() : e;
        if(cause != null) {
            Toaster.showShort(this, cause.getMessage());
        }
    }
}
