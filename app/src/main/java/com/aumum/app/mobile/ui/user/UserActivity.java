package com.aumum.app.mobile.ui.user;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.contact.DeleteContactListener;

public class UserActivity extends ProgressDialogActivity
        implements DeleteContactListener.OnProgressListener {

    public static final String INTENT_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);
    }

    @Override
    public void onDeleteContactStart() {
        progress.setMessageId(R.string.info_submitting_delete_contact);
        showProgress();
    }

    @Override
    public void onDeleteContactFinish() {
        hideProgress();
    }
}
