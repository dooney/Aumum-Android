package com.aumum.app.mobile.ui.user;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class AddContactActivity extends ProgressDialogActivity {

    @Inject ChatService chatService;
    private String toUserId;

    public static final String INTENT_TO_USER_ID = "toUserId";
    public static final String INTENT_FROM_USER_NAME = "fromUserName";

    private SafeAsyncTask<Boolean> task;

    @InjectView(R.id.et_intro) protected EditText introEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_contact);
        ButterKnife.inject(this);

        toUserId = getIntent().getStringExtra(INTENT_TO_USER_ID);
        final String userName = getIntent().getStringExtra(INTENT_FROM_USER_NAME);
        introEditText.setText(getString(R.string.label_add_contact_intro, userName));
        progress.setMessageId(R.string.info_submitting_add_contact);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.label_send))
                .setActionView(R.layout.menuitem_button_send)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        addContact();
        return super.onOptionsItemSelected(item);
    }

    private void addContact() {
        showProgress();

        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                String intro = "";
                if (introEditText.getText() != null) {
                    intro = introEditText.getText().toString();
                }
                chatService.addContact(toUserId, intro);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showLong(AddContactActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                if (success) {
                    Toaster.showLong(AddContactActivity.this, R.string.info_add_contact_sent);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toaster.showLong(AddContactActivity.this, R.string.error_add_contact);
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                task = null;
            }
        };
        task.execute();
    }
}
