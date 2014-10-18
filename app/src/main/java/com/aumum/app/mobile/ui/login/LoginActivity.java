package com.aumum.app.mobile.ui.login;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static com.aumum.app.mobile.ui.splash.SplashActivity.PARAM_AUTHTOKEN_TYPE;
import static com.aumum.app.mobile.ui.splash.SplashActivity.SHOW_SIGN_UP;
import static com.aumum.app.mobile.ui.splash.SplashActivity.SHOW_RESET_PASSWORD;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.R.id;
import com.aumum.app.mobile.R.layout;
import com.aumum.app.mobile.core.RestService;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.User;
import com.aumum.app.mobile.core.UserStore;
import com.aumum.app.mobile.events.UnAuthorizedErrorEvent;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.view.ProgressDialog;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

/**
 * Activity to authenticate the user against an API (example API on Parse.com)
 */
public class LoginActivity extends ActionBarActivity {
    private AccountManager accountManager;

    private UserStore userStore;

    @Inject
    RestService restService;
    @Inject Bus bus;

    @InjectView(id.et_username) protected EditText usernameText;
    @InjectView(id.et_password) protected EditText passwordText;
    @InjectView(id.b_signin) protected Button signInButton;
    @InjectView(id.t_forgot_password) protected TextView forgotPasswordText;
    @InjectView(id.t_join_now) protected TextView joinNowText;

    private final TextWatcher watcher = validationTextWatcher();
    private final ProgressDialog progress = ProgressDialog.newInstance(R.string.message_authenticating);

    private SafeAsyncTask<Boolean> authenticationTask;
    private String authToken;
    private String authTokenType;

    private String username;
    private String password;
    private String userId;

    /**
     * In this instance the token is simply the sessionId returned from Parse.com. This could be a
     * oauth token or some other type of timed token that expires/etc. We're just using the parse.com
     * sessionId to prove the example of how to utilize a token.
     */
    private String token;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Injector.inject(this);

        accountManager = AccountManager.get(this);

        userStore = UserStore.getInstance(this);

        final Intent intent = getIntent();
        authTokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);

        setContentView(layout.login_activity);

        ButterKnife.inject(this);

        passwordText.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && signInButton.isEnabled()) {
                    handleLogin(signInButton);
                    return true;
                }
                return false;
            }
        });

        passwordText.setOnEditorActionListener(new OnEditorActionListener() {

            public boolean onEditorAction(final TextView v, final int actionId,
                                          final KeyEvent event) {
                if (actionId == IME_ACTION_DONE && signInButton.isEnabled()) {
                    handleLogin(signInButton);
                    return true;
                }
                return false;
            }
        });

        usernameText.addTextChangedListener(watcher);
        passwordText.addTextChangedListener(watcher);

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent();
                intent.putExtra(SHOW_RESET_PASSWORD, true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        joinNowText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent();
                intent.putExtra(SHOW_SIGN_UP, true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
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
        bus.register(this);
        updateUIWithValidation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    private void updateUIWithValidation() {
        final boolean populated = populated(usernameText) && populated(passwordText);
        signInButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    @Subscribe
    public void onUnAuthorizedErrorEvent(UnAuthorizedErrorEvent unAuthorizedErrorEvent) {
        // Could not authorize for some reason.
        Toaster.showLong(LoginActivity.this, R.string.error_bad_credentials);
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

    /**
     * Handles onClick event on the Submit button. Sends username/password to
     * the server for authentication.
     * <p/>
     * Specified by android:onClick="handleLogin" in the layout xml
     *
     * @param view
     */
    public void handleLogin(final View view) {
        if (authenticationTask != null) {
            return;
        }

        username = usernameText.getText().toString();
        password = passwordText.getText().toString();
        showProgress();

        authenticationTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User response = restService.authenticate(username, password);
                if (!response.getEmailVerified()) {
                    throw new Exception(getString(R.string.error_authentication_email_not_verified));
                }
                userStore.saveCurrentUser(response);
                token = response.getSessionToken();
                userId = response.getObjectId();

                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showLong(LoginActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean authSuccess) {
                onAuthenticationResult(authSuccess);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                authenticationTask = null;
            }
        };
        authenticationTask.execute();
    }

    /**
     * Called when response is received from the server for authentication
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller. Also sets
     * the authToken in AccountManager for this account.
     */

    protected void finishLogin() {
        final Account account = new Account(userId, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, password, null);
        authToken = token;
        accountManager.setAuthToken(account, Constants.Auth.AUTHTOKEN_TYPE, authToken);

        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_NAME, username);
        intent.putExtra(KEY_ACCOUNT_TYPE, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

        if (authTokenType != null
                && authTokenType.equals(Constants.Auth.AUTHTOKEN_TYPE)) {
            intent.putExtra(KEY_AUTHTOKEN, authToken);
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param result
     */
    public void onAuthenticationResult(final boolean result) {
        if (result) {
            finishLogin();
        } else {
            Toaster.showLong(LoginActivity.this,
                    R.string.error_authentication);
        }
    }
}