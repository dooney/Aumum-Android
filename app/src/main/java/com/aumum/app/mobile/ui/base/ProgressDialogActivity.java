package com.aumum.app.mobile.ui.base;

import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.ui.view.ProgressDialog;

/**
 * Created by Administrator on 8/11/2014.
 */
public abstract class ProgressDialogActivity extends ActionBarActivity
        implements ProgressListener {

    protected final ProgressDialog progress = ProgressDialog.newInstance();

    public void setMessage(int resId) {
        progress.setMessageId(resId);
    }

    public void showProgress() {
        if (!progress.isAdded()) {
            progress.show(getFragmentManager(), null);
        }
    }

    public void hideProgress() {
        if (progress != null && progress.getActivity() != null) {
            progress.dismissAllowingStateLoss();
        }
    }
}
