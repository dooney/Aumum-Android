package com.aumum.app.mobile.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static com.aumum.app.mobile.ui.splash.SplashActivity.KEY_ACCOUNT_EMAIL;

public class ResetPasswordActivity extends ProgressDialogActivity {

    @Inject
    RestService restService;

    @InjectView(R.id.et_email) protected EditText emailText;
    @InjectView(R.id.b_reset_password) protected Button submitButton;

    private final TextWatcher watcher = validationTextWatcher();

    private SafeAsyncTask<Boolean> resetPasswordTask;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        setContentView(R.layout.activity_reset_password);

        ButterKnife.inject(this);

        progress.setMessageId(R.string.info_submitting_password_reset);

        emailText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && submitButton.isEnabled()) {
                    handleSubmit(submitButton);
                    return true;
                }
                return false;
            }
        });

        emailText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(final TextView v, final int actionId,
                                          final KeyEvent event) {
                if (actionId == IME_ACTION_DONE && submitButton.isEnabled()) {
                    handleSubmit(submitButton);
                    return true;
                }
                return false;
            }
        });

        emailText.addTextChangedListener(watcher);

        Animation.flyIn(this);
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIWithValidation();
    }

    private void updateUIWithValidation() {
        final boolean populated = populated(emailText);
        submitButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    public void handleSubmit(final View view) {
        if (resetPasswordTask != null) {
            return;
        }

        email = emailText.getText().toString();
        showProgress();

        resetPasswordTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.resetPassword(email);

                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(ResetPasswordActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                onSubmitResult(success);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                resetPasswordTask = null;
            }
        };
        resetPasswordTask.execute();
    }

    private void finishSubmit() {
        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_EMAIL, email);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onSubmitResult(final Boolean result) {
        if (result) {
            finishSubmit();
        } else {
            Toaster.showShort(ResetPasswordActivity.this,
                    R.string.error_reset_password);
        }
    }
}
