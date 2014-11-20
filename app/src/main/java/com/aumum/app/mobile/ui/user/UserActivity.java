package com.aumum.app.mobile.ui.user;

import android.content.Intent;
import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.contact.DeleteContactListener;
import com.github.kevinsawicki.wishlist.Toaster;

public class UserActivity extends ProgressDialogActivity
        implements DeleteContactListener.OnProgressListener,
                   DeleteContactListener.OnActionListener {

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



    @Override
    public void onDeleteContactSuccess(String contactId) {
        Toaster.showLong(this, R.string.info_contact_deleted);
        final Intent intent = new Intent();
        intent.putExtra(UserActivity.INTENT_USER_ID, contactId);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDeleteContactFailed() {
        Toaster.showLong(this, R.string.error_delete_contact);
    }
}
