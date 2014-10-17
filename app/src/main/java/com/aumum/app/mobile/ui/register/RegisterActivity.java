package com.aumum.app.mobile.ui.register;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.DialogInterface;
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
import com.aumum.app.mobile.core.RestService;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.view.ProgressDialog;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Views;
import retrofit.RetrofitError;

import static android.R.layout.simple_dropdown_item_1line;
import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static com.aumum.app.mobile.ui.splash.SplashActivity.KEY_ACCOUNT_EMAIL;
import static com.aumum.app.mobile.ui.splash.SplashActivity.SHOW_SIGN_IN;

public class RegisterActivity extends ActionBarActivity {
    private AccountManager accountManager;

    @Inject
    RestService restService;

    @InjectView(R.id.et_username) protected EditText usernameText;
    @InjectView(R.id.et_password) protected EditText passwordText;
    @InjectView(R.id.et_email) protected AutoCompleteTextView emailText;
    @InjectView(R.id.b_area) protected Button areaButton;
    @InjectView(R.id.b_signup) protected Button signUpButton;
    @InjectView(R.id.t_prompt_sign_in) protected TextView promptSignInText;

    private final TextWatcher watcher = validationTextWatcher();
    private final ProgressDialog progress = ProgressDialog.newInstance(R.string.message_processing);

    private SafeAsyncTask<Boolean> registerTask;

    private String username;
    private String password;
    private String email;
    private int area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        accountManager = AccountManager.get(this);

        setContentView(R.layout.activity_register);

        Views.inject(this);

        emailText.setAdapter(new ArrayAdapter<String>(this,
                simple_dropdown_item_1line, userEmailAccounts()));

        emailText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && signUpButton.isEnabled()) {
                    handleRegister(signUpButton);
                    return true;
                }
                return false;
            }
        });

        emailText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(final TextView v, final int actionId,
                                          final KeyEvent event) {
                if (actionId == IME_ACTION_DONE && signUpButton.isEnabled()) {
                    handleRegister(signUpButton);
                    return true;
                }
                return false;
            }
        });

        usernameText.addTextChangedListener(watcher);
        passwordText.addTextChangedListener(watcher);
        emailText.addTextChangedListener(watcher);
        areaButton.addTextChangedListener(watcher);

        areaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showDialog(RegisterActivity.this, R.string.label_area, Constants.AREA_OPTIONS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        area = i;
                        areaButton.setText(Constants.AREA_OPTIONS[i]);
                    }
                });
            }
        });

        promptSignInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent();
                intent.putExtra(SHOW_SIGN_IN, true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
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
        final boolean populated = populated(usernameText) &&
                populated(passwordText) &&
                populated(emailText) &&
                areaButton.getText().length() > 0;
        signUpButton.setEnabled(populated);
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

    public void handleRegister(final View view) {
        if (registerTask != null) {
            return;
        }

        username = usernameText.getText().toString();
        password = passwordText.getText().toString();
        email = emailText.getText().toString();
        showProgress();

        registerTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.register(username, password, email, area);

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
        intent.putExtra(KEY_ACCOUNT_EMAIL, email);
        setResult(RESULT_OK, intent);
        finish();
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
