package com.aumum.app.mobile.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.ui.base.AccountAuthenticatorActivity;
import com.aumum.app.mobile.ui.account.LoginActivity;
import com.aumum.app.mobile.ui.account.RegisterActivity;
import com.aumum.app.mobile.ui.account.ResetPasswordActivity;
import com.aumum.app.mobile.ui.account.ResetPasswordSuccessActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SplashActivity extends AccountAuthenticatorActivity {

    @InjectView(R.id.b_signUp) protected Button signUpButton;
    @InjectView(R.id.b_signIn) protected Button signInButton;

    public static final String SHOW_SIGN_IN = "showSignIn";
    public static final String SHOW_SIGN_UP = "showSignUp";
    public static final String SHOW_RESET_PASSWORD = "showResetPassword";

    private final int LOGIN_REQ_CODE = 1031;
    private final int REGISTER_REQ_CODE = 1032;
    private final int RESET_PASSWORD_REQ_CODE = 1033;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegister();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogin();
            }
        });
    }

    public void showLogin() {
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQ_CODE);
    }

    public void showRegister() {
        final Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, REGISTER_REQ_CODE);
    }

    public void showForgotPassword() {
        final Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivityForResult(intent, RESET_PASSWORD_REQ_CODE);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOGIN_REQ_CODE:
                onLoginResult(resultCode, data);
                break;
            case REGISTER_REQ_CODE:
                onRegistrationResult(resultCode, data);
                break;
            case RESET_PASSWORD_REQ_CODE:
                onResetPasswordResult(resultCode, data);
                break;
            default:
                break;
        }
        return;
    }

    private void onLoginResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data.getBooleanExtra(SHOW_SIGN_UP, false)) {
                showRegister();
            } else if (data.getBooleanExtra(SHOW_RESET_PASSWORD, false)) {
                showForgotPassword();
            } else {
                setAccountAuthenticatorResult(data.getExtras());
                finish();
            }
        }
    }

    private void onRegistrationResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data.getBooleanExtra(SHOW_SIGN_IN, false)) {
                showLogin();
            } else {
                setAccountAuthenticatorResult(data.getExtras());
                finish();
            }
        }
    }

    private void onResetPasswordResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String email = data.getStringExtra(Constants.Auth.KEY_ACCOUNT_EMAIL);
            final Intent intent = new Intent(this, ResetPasswordSuccessActivity.class);
            intent.putExtra(Constants.Auth.KEY_ACCOUNT_EMAIL, email);
            startActivity(intent);
        }
    }
}
