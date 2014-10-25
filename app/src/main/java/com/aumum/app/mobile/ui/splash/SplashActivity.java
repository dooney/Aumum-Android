package com.aumum.app.mobile.ui.splash;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.AccountAuthenticatorActivity;
import com.aumum.app.mobile.ui.login.LoginActivity;
import com.aumum.app.mobile.ui.register.RegisterActivity;
import com.aumum.app.mobile.ui.register.RegistrationSuccessActivity;
import com.aumum.app.mobile.ui.account.ResetPasswordActivity;
import com.aumum.app.mobile.ui.account.ResetPasswordSuccessActivity;
import com.aumum.app.mobile.utils.ImageUtils;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AccountAuthenticatorActivity {
    private PagerAdapter mAdapter;
    private ViewPager mPager;
    private CirclePageIndicator mIndicator;

    private String authTokenType;

    /**
     * PARAM_AUTHTOKEN_TYPE
     */
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    public static final String KEY_ACCOUNT_EMAIL = "authEmail";

    public static final String SHOW_SIGN_IN = "showSignIn";

    public static final String SHOW_SIGN_UP = "showSignUp";

    public static final String SHOW_RESET_PASSWORD = "showResetPassword";

    private final int LOGIN_REQ_CODE = 1031;

    private final int REGISTER_REQ_CODE = 1032;

    private final int RESET_PASSWORD_REQ_CODE = 1033;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        final Intent intent = getIntent();
        authTokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);

        initSplashViews();
    }

    private void initSplashViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> views = new ArrayList<View>();
        TypedArray images = getResources().obtainTypedArray(R.array.splash_images);
        for (int i = 0; i < images.length(); i++) {
            View splashView = inflater.inflate(R.layout.view_splash, null);
            ImageView imageBackground = (ImageView) splashView.findViewById(R.id.image_background);
            Bitmap bm = ImageUtils.readBitmap(this, images.getResourceId(i, -1));
            imageBackground.setImageBitmap(bm);
            views.add(splashView);
        }
        mAdapter = new SplashViewPagerAdapter(views);

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }

    public void showLogin(final View view) {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(PARAM_AUTHTOKEN_TYPE, authTokenType);
        startActivityForResult(intent, LOGIN_REQ_CODE);
    }

    public void showRegister(final View view) {
        final Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra(PARAM_AUTHTOKEN_TYPE, authTokenType);
        startActivityForResult(intent, REGISTER_REQ_CODE);
    }

    public void showForgotPassword(final View view) {
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
                showRegister(null);
            } else if (data.getBooleanExtra(SHOW_RESET_PASSWORD, false)) {
                showForgotPassword(null);
            } else {
                setAccountAuthenticatorResult(data.getExtras());
                finish();
            }
        }
    }

    private void onRegistrationResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data.getBooleanExtra(SHOW_SIGN_IN, false)) {
                showLogin(null);
            } else {
                String email = data.getStringExtra(KEY_ACCOUNT_EMAIL);
                final Intent intent = new Intent(this, RegistrationSuccessActivity.class);
                intent.putExtra(KEY_ACCOUNT_EMAIL, email);
                startActivity(intent);
            }
        }
    }

    private void onResetPasswordResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String email = data.getStringExtra(KEY_ACCOUNT_EMAIL);
            final Intent intent = new Intent(this, ResetPasswordSuccessActivity.class);
            intent.putExtra(KEY_ACCOUNT_EMAIL, email);
            startActivity(intent);
        }
    }
}
