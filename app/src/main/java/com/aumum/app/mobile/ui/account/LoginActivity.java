package com.aumum.app.mobile.ui.account;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static com.aumum.app.mobile.ui.splash.SplashActivity.SHOW_SIGN_UP;
import static com.aumum.app.mobile.ui.splash.SplashActivity.SHOW_RESET_PASSWORD;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.R.id;
import com.aumum.app.mobile.R.layout;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.Repository;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.events.UnAuthorizedErrorEvent;
import com.aumum.app.mobile.ui.base.AuthenticateActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.view.ClearEditText;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;
import com.aumum.app.mobile.utils.EMChatUtils;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.Strings;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

/**
 * Activity to authenticate the user against an API (example API on Parse.com)
 */
public class LoginActivity extends AuthenticateActivity {

    @Inject RestService restService;
    @Inject Repository repository;
    @Inject Bus bus;

    @InjectView(id.text_country) protected TextView countryText;
    @InjectView(id.text_country_code) protected TextView countryCodeText;
    @InjectView(id.et_phone) protected ClearEditText phoneText;
    @InjectView(id.et_password) protected ClearEditText passwordText;
    @InjectView(id.b_sign_in) protected View signInButton;
    @InjectView(id.b_join_now) protected TextView joinNowButton;
    @InjectView(id.t_forgot_password) protected TextView forgotPasswordText;

    private final TextWatcher watcher = validationTextWatcher();

    private SafeAsyncTask<Boolean> task;
    private String password;
    private User user;

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
                final ArrayList<String> countries = new ArrayList<>(
                        Constants.Map.COUNTRY.keySet());
                new ListViewDialog(LoginActivity.this,
                        getString(R.string.label_select_your_country),
                        countries,
                        new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        String country = countries.get(i);
                        countryText.setText(country);
                        countryCodeText.setText(
                                Constants.Map.COUNTRY.get(country));
                    }
                }).show();
            }
        });
        phoneText.setClearButtonResId(R.drawable.ic_fa_times_y);
        phoneText.addTextChangedListener(watcher);
        passwordText.setClearButtonResId(R.drawable.ic_fa_times_y);
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
        joinNowButton.setOnClickListener(new View.OnClickListener() {
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
        final boolean populated = populated(phoneText) && populated(passwordText);
        if (signInButton != null) {
            signInButton.setEnabled(populated);
        }
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    @Subscribe
    public void onUnAuthorizedErrorEvent(UnAuthorizedErrorEvent unAuthorizedErrorEvent) {
        // Could not authorize for some reason.
        showMsg(R.string.error_bad_credentials);
    }

    public void login() {
        if (task != null) {
            return;
        }
        final String mobile = countryCodeText.getText() +
                Strings.removeLeadingZeros(phoneText.getText().toString());
        EditTextUtils.hideSoftInput(phoneText);
        password = passwordText.getText().toString();
        EditTextUtils.hideSoftInput(passwordText);
        showProgress();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                user = restService.authenticate(mobile, password);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    showError(e);
                }
                hideProgress();
            }

            @Override
            public void onSuccess(final Boolean success) {
                resetLocalDb();
                resetChatServer(user);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }

    private void resetLocalDb() {
        repository.reset();
    }

    private void resetChatServer(final User user) {
        EMChatUtils.logOut();
        EMChatUtils.authenticate(user.getChatId(), password,
                new EMChatUtils.OnAuthenticateListener() {
                    @Override
                    public void onSuccess() {
                        finishAuthentication(user.getObjectId(), password, user.getSessionToken());
                        hideProgress();
                    }

                    @Override
                    public void onError(String message) {
                        showMsg(message);
                        hideProgress();
                    }
                });
    }
}
