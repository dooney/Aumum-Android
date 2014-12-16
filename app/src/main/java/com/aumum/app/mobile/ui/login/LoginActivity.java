package com.aumum.app.mobile.ui.login;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static com.aumum.app.mobile.ui.splash.SplashActivity.SHOW_SIGN_UP;
import static com.aumum.app.mobile.ui.splash.SplashActivity.SHOW_RESET_PASSWORD;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.events.UnAuthorizedErrorEvent;
import com.aumum.app.mobile.ui.base.AuthenticateActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.EditTextUtils;
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
public class LoginActivity extends AuthenticateActivity {

    @Inject RestService restService;
    @Inject Bus bus;

    @InjectView(R.id.text_country) protected TextView countryText;
    @InjectView(R.id.text_country_code) protected TextView countryCodeText;
    @InjectView(R.id.et_phone) protected EditText phoneText;
    @InjectView(id.et_password) protected EditText passwordText;
    @InjectView(id.b_sign_in) protected Button signInButton;
    @InjectView(id.t_forgot_password) protected TextView forgotPasswordText;
    @InjectView(id.t_join_now) protected TextView joinNowText;

    private final TextWatcher watcher = validationTextWatcher();

    private SafeAsyncTask<Boolean> task;
    private String userId;
    private String password;
    private String token;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Injector.inject(this);
        setContentView(layout.activity_login);
        ButterKnife.inject(this);

        progress.setMessageId(R.string.info_authenticating);

        countryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String countryOptions[] = Constants.Options.COUNTRY_OPTIONS;
                DialogUtils.showDialog(LoginActivity.this, countryOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        countryText.setText(countryOptions[i]);
                        switch (i) {
                            case 1:
                                countryCodeText.setText("0064");
                                break;
                            case 2:
                                countryCodeText.setText("0086");
                                break;
                            default:
                                countryCodeText.setText("0061");
                                break;
                        }
                    }
                });
            }
        });
        phoneText.addTextChangedListener(watcher);
        passwordText.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && signInButton.isEnabled()) {
                    login();
                    return true;
                }
                return false;
            }
        });
        passwordText.setOnEditorActionListener(new OnEditorActionListener() {

            public boolean onEditorAction(final TextView v, final int actionId,
                                          final KeyEvent event) {
                if (actionId == IME_ACTION_DONE && signInButton.isEnabled()) {
                    login();
                    return true;
                }
                return false;
            }
        });
        passwordText.addTextChangedListener(watcher);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
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
        bus.register(this);
        updateUIWithValidation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    private void updateUIWithValidation() {
        final boolean populated = populated(phoneText) && populated(passwordText);
        signInButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    @Subscribe
    public void onUnAuthorizedErrorEvent(UnAuthorizedErrorEvent unAuthorizedErrorEvent) {
        // Could not authorize for some reason.
        Toaster.showShort(LoginActivity.this, R.string.error_bad_credentials);
    }

    public void login() {
        if (task != null) {
            return;
        }

        final String mobile = countryCodeText.getText() + phoneText.getText().toString();
        EditTextUtils.hideSoftInput(phoneText);
        password = passwordText.getText().toString();
        EditTextUtils.hideSoftInput(passwordText);
        showProgress();

        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User response = restService.authenticate(mobile, password);
                userId = response.getObjectId();
                token = response.getSessionToken();
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(LoginActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean authSuccess) {
                if (authSuccess) {
                    finishAuthentication(userId, password, token);
                } else {
                    Toaster.showShort(LoginActivity.this,
                            R.string.error_authentication);
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
