package com.aumum.app.mobile.core.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;

import com.aumum.app.mobile.core.Constants;

import javax.inject.Inject;


/**
 * Class used for logging a user out.
 */
public class LogoutService {

    protected final Context context;
    protected final AccountManager accountManager;

    @Inject
    public LogoutService(final Context context, final AccountManager accountManager) {
        this.context = context;
        this.accountManager = accountManager;
    }

    public boolean logout() throws Exception {
        final AccountManager accountManagerWithContext = AccountManager.get(context);
        if (accountManagerWithContext != null) {
            final Account[] accounts = accountManagerWithContext
                    .getAccountsByType(Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
            if (accounts.length > 0) {
                final AccountManagerFuture<Boolean> removeAccountFuture
                        = accountManagerWithContext.removeAccount(accounts[0], null, null);
                return removeAccountFuture.getResult();
            }
        }
        return false;
    }
}
