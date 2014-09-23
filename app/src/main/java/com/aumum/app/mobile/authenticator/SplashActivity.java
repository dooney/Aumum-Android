package com.aumum.app.mobile.authenticator;

import android.content.Intent;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import com.aumum.app.mobile.R;
import com.viewpagerindicator.CirclePageIndicator;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;

public class SplashActivity extends ActionBarAccountAuthenticatorActivity {
    private FragmentPagerAdapter mAdapter;
    private ViewPager mPager;
    private CirclePageIndicator mIndicator;

    private String authTokenType;

    /**
     * PARAM_AUTHTOKEN_TYPE
     */
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    private final int LOGIN_REQ_CODE = 1031;

    private final int REGISTER_REQ_CODE = 1032;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Intent intent = getIntent();
        authTokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);

        mAdapter = new SplashFragmentPagerAdapter(getSupportFragmentManager());

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

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOGIN_REQ_CODE:
                onLoginResult(resultCode, data);
                break;
            case REGISTER_REQ_CODE:
                onRegistrationResult(resultCode, data);
                break;
            default:
                break;
        }
        return;
    }

    private void onLoginResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setAccountAuthenticatorResult(data.getExtras());
            finish();
        }
    }

    private void onRegistrationResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            /*String email = data.getStringExtra(KEY_ACCOUNT_NAME);
            final Intent intent = new Intent(this, RegistrationSuccessActivity.class);
            intent.putExtra(KEY_ACCOUNT_NAME, email);
            startActivity(intent);*/
        }
    }
}
