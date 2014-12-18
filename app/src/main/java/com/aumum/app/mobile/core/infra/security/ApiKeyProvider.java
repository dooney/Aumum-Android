

package com.aumum.app.mobile.core.infra.security;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AccountsException;
import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;

import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static com.aumum.app.mobile.core.Constants.Auth.AUTH_TOKEN_TYPE;
import static com.aumum.app.mobile.core.Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE;

/**
 * Bridge class that obtains a API key for the currently configured account
 */
public class ApiKeyProvider {

    private AccountManager accountManager;

    public ApiKeyProvider(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    /**
     * This call blocks, so shouldn't be called on the UI thread.
     * This call is what makes the login screen pop up. If the user has
     * not logged in there will no accounts in the {@link android.accounts.AccountManager}
     * and therefore the Activity that is referenced in the
     * {@link AccountAuthenticator} will get started.
     * If you want to remove the authentication then you can comment out the code below and return a string such as
     * "foo" and the authentication process will not be kicked off. Alternatively, you can remove this class
     * completely and clean up any references to the authenticator.
     *
     *
     * @return API key to be used for authorization with a
     * {@link com.aumum.app.mobile.core.service.RestService} instance
     * @throws AccountsException
     * @throws IOException
     */
    public String getAuthKey(final Activity activity) throws AccountsException, IOException {
        final AccountManagerFuture<Bundle> accountManagerFuture
                = accountManager.getAuthTokenByFeatures(BOOTSTRAP_ACCOUNT_TYPE,
                AUTH_TOKEN_TYPE, new String[0], activity, null, null, null, null);

        return accountManagerFuture.getResult().getString(KEY_AUTHTOKEN);
    }

    public String getAuthToken() {
        Account accounts[] = accountManager.getAccountsByType(BOOTSTRAP_ACCOUNT_TYPE);
        if (accounts.length > 0) {
            return accountManager.peekAuthToken(accounts[0], AUTH_TOKEN_TYPE);
        }
        return null;
    }

    public String getAuthUserId() {
        Account accounts[] = accountManager.getAccountsByType(BOOTSTRAP_ACCOUNT_TYPE);
        if (accounts.length > 0) {
            return accounts[0].name;
        }
        return null;
    }

    public String getAuthPassword() {
        Account accounts[] = accountManager.getAccountsByType(BOOTSTRAP_ACCOUNT_TYPE);
        if (accounts.length > 0) {
            return accountManager.getPassword(accounts[0]);
        }
        return null;
    }
}
