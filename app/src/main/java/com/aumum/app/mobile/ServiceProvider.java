
package com.aumum.app.mobile;

import android.accounts.AccountsException;
import android.app.Activity;

import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.service.RestService;

import java.io.IOException;

import retrofit.RestAdapter;

/**
 * Provider for a {@link com.aumum.app.mobile.core.service.RestService} instance
 */
public class ServiceProvider {

    private RestAdapter restAdapter;
    private ApiKeyProvider keyProvider;

    public ServiceProvider(RestAdapter restAdapter, ApiKeyProvider keyProvider) {
        this.restAdapter = restAdapter;
        this.keyProvider = keyProvider;
    }

    /**
     * Get service for configured key provider
     * <p/>
     * This method gets an auth key and so it blocks and shouldn't be called on the main thread.
     *
     * @return bootstrap service
     * @throws IOException
     * @throws AccountsException
     */
    public RestService getService(final Activity activity)
            throws IOException, AccountsException {
        // The call to keyProvider.getAuthKey(...) is what initiates the login screen. Call that now.
        keyProvider.getAuthKey(activity);

        // TODO: See how that affects the bootstrap service.
        return new RestService(restAdapter);
    }
}
