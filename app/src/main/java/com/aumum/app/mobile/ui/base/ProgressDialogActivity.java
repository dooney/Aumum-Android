package com.aumum.app.mobile.ui.base;

import android.os.Bundle;

import com.aumum.app.mobile.ui.view.dialog.ProgressDialog;

/**
 * Created by Administrator on 8/11/2014.
 */
public abstract class ProgressDialogActivity extends BaseActionBarActivity
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
}
