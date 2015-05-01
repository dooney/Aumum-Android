package com.aumum.app.mobile.ui.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.ui.view.dialog.ProgressDialog;
import com.github.kevinsawicki.wishlist.Toaster;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 8/11/2014.
 */
public abstract class ProgressDialogActivity extends ActionBarActivity
        implements ProgressListener {

    protected ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(this);
    }

    public void setMessage(int resId) {
        progress.setMessageId(resId);
    }

    public void showProgress() {
        if (!progress.isShowing()) {
            progress.show();
        }
    }

    public void hideProgress() {
        progress.dismiss();
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
