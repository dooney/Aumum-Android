package com.aumum.app.mobile.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import com.aumum.app.mobile.ui.TextWatcherAdapter;
import com.aumum.app.mobile.util.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.Views;
import retrofit.RetrofitError;

import static android.R.layout.simple_dropdown_item_1line;
import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

public class RegisterActivity extends ActionBarActivity {

    private AccountManager accountManager;

    @InjectView(R.id.et_email) protected AutoCompleteTextView emailText;
    @InjectView(R.id.et_password) protected EditText passwordText;
    @InjectView(R.id.et_username) protected EditText usernameText;
    @InjectView(R.id.b_signup) protected Button signUpButton;

    private final TextWatcher watcher = validationTextWatcher();

    private SafeAsyncTask<Boolean> registerTask;

    private String email;

    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        accountManager = AccountManager.get(this);

        setContentView(R.layout.activity_register);

        Views.inject(this);

        emailText.setAdapter(new ArrayAdapter<String>(this,
                simple_dropdown_item_1line, userEmailAccounts()));

        passwordText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && signUpButton.isEnabled()) {
                    handleRegister(signUpButton);
                    return true;
                }
                return false;
            }
        });

        passwordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(final TextView v, final int actionId,
                                          final KeyEvent event) {
                if (actionId == IME_ACTION_DONE && signUpButton.isEnabled()) {
                    handleRegister(signUpButton);
                    return true;
                }
                return false;
            }
        });

        emailText.addTextChangedListener(watcher);
        passwordText.addTextChangedListener(watcher);
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
        final boolean populated = populated(emailText) && populated(passwordText) && populated(usernameText);
        signUpButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    public void handleRegister(final View view) {
        if (registerTask != null) {
            return;
        }

        showProgress();

        registerTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                //User loginResponse = bootstrapService.authenticate(email, password);
                //token = loginResponse.getSessionToken();

                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                // Retrofit Errors are handled inside of the {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showLong(RegisterActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                onRegistrationResult(success);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                registerTask = null;
            }
        };
        registerTask.execute();
    }

    private void finishRegistration() {
        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_NAME, email);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Hide progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void hideProgress() {
        dismissDialog(0);
    }

    /**
     * Show progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void showProgress() {
        showDialog(0);
    }

    public void onRegistrationResult(final Boolean result) {
        if (result) {
            finishRegistration();
        } else {
            Toaster.showLong(RegisterActivity.this,
                    R.string.message_auth_failed_new_account);
        }
    }
}
