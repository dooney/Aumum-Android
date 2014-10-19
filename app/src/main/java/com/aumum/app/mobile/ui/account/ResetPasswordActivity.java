package com.aumum.app.mobile.ui.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.view.ProgressDialog;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

import static android.R.layout.simple_dropdown_item_1line;
import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static com.aumum.app.mobile.ui.splash.SplashActivity.KEY_ACCOUNT_EMAIL;

public class ResetPasswordActivity extends ActionBarActivity {
    private AccountManager accountManager;

    @Inject
    RestService restService;

    @InjectView(R.id.et_email) protected AutoCompleteTextView emailText;
    @InjectView(R.id.b_reset_password) protected Button submitButton;

    private final TextWatcher watcher = validationTextWatcher();
    private final ProgressDialog progress = ProgressDialog.newInstance(R.string.message_submitting_password_reset);

    private SafeAsyncTask<Boolean> resetPasswordTask;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        accountManager = AccountManager.get(this);

        setContentView(R.layout.activity_reset_password);

        ButterKnife.inject(this);

        emailText.setAdapter(new ArrayAdapter<String>(this,
                simple_dropdown_item_1line, userEmailAccounts()));

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
    }

    private List<String> userEmailAccounts() {
        final Account[] accounts = accountManager.getAccountsByType("com.google");
        final List<String> emailAddresses = new ArrayList<String>(accounts.length);
        for (final Account account : accounts) {
            emailAddresses.add(account.name);
        }
        return emailAddresses;
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

    private synchronized void showProgress() {
        if (!progress.isAdded()) {
            progress.show(getFragmentManager(), null);
        }
    }

    private synchronized void hideProgress() {
        if (progress != null && progress.getActivity() != null) {
            progress.dismissAllowingStateLoss();
        }
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
                        Toaster.showLong(ResetPasswordActivity.this, cause.getMessage());
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
            Toaster.showLong(ResetPasswordActivity.this,
                    R.string.error_reset_password);
        }
    }
}
