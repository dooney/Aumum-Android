package com.aumum.app.mobile.ui.base;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;

import com.aumum.app.mobile.core.Constants;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;

/**
 * Created by Administrator on 16/12/2014.
 */
public abstract class AuthenticateActivity extends ProgressDialogActivity {
    protected AccountManager accountManager;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        accountManager = AccountManager.get(this);
    }

    protected void finishAuthentication(String userId, String password, String token) {
        final Account account = new Account(userId, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, password, null);
        accountManager.setAuthToken(account, Constants.Auth.AUTH_TOKEN_TYPE, token);

        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_NAME, userId);
        intent.putExtra(KEY_ACCOUNT_TYPE, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
        intent.putExtra(KEY_AUTHTOKEN, token);
        setResult(RESULT_OK, intent);
        finish();
    }
}
