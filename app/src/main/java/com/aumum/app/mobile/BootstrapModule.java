package com.aumum.app.mobile;

import android.accounts.AccountManager;
import android.content.Context;

import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.service.MessageListener;
import com.aumum.app.mobile.core.service.NotificationListener;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.party.PartyListFragment;
import com.aumum.app.mobile.ui.party.PartyOwnerActionListener;
import com.aumum.app.mobile.ui.party.PartyPostActivity;
import com.aumum.app.mobile.ui.account.ResetPasswordActivity;
import com.aumum.app.mobile.ui.login.LoginActivity;
import com.aumum.app.mobile.core.service.LogoutService;
import com.aumum.app.mobile.ui.register.RegisterActivity;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.MessageStore;
import com.aumum.app.mobile.core.dao.PartyCommentStore;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.utils.PostFromAnyThreadBus;
import com.aumum.app.mobile.core.api.RestAdapterRequestInterceptor;
import com.aumum.app.mobile.core.api.RestErrorHandler;
import com.aumum.app.mobile.core.api.UserAgentProvider;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.ui.user.FollowListener;
import com.aumum.app.mobile.ui.party.JoinListener;
import com.aumum.app.mobile.ui.party.LikeListener;
import com.aumum.app.mobile.ui.main.MainActivity;
import com.aumum.app.mobile.ui.message.MessageListFragment;
import com.aumum.app.mobile.ui.party.PartyCommentsFragment;
import com.aumum.app.mobile.ui.party.PartyDetailsFragment;
import com.aumum.app.mobile.ui.user.UserFragment;
import com.aumum.app.mobile.ui.user.UserProfileImageActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module(
        complete = false,

        injects = {
                BootstrapApplication.class,
                LoginActivity.class,
                RegisterActivity.class,
                ResetPasswordActivity.class,
                MainActivity.class,
                PartyPostActivity.class,
                PartyStore.class,
                FollowListener.class,
                UserStore.class,
                MessageListFragment.class,
                MessageStore.class,
                JoinListener.class,
                LikeListener.class,
                PartyCommentStore.class,
                PartyCommentsFragment.class,
                UserFragment.class,
                PartyDetailsFragment.class,
                UserProfileImageActivity.class,
                PartyOwnerActionListener.class,
                PartyListFragment.class
        }
)
public class BootstrapModule {

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new PostFromAnyThreadBus();
    }

    @Singleton
    @Provides
    NotificationListener provideNotificationListener(final Context context) {
        return new NotificationListener(context);
    }

    @Provides
    MessageListener provideMessageListener(Bus bus, RestService restService, NotificationListener notificationListener) {
        return new MessageListener(bus, restService, notificationListener);
    }

    @Provides
    @Singleton
    LogoutService provideLogoutService(final Context context, final AccountManager accountManager) {
        return new LogoutService(context, accountManager);
    }

    @Provides
    RestService provideRestService(RestAdapter restAdapter) {
        return new RestService(restAdapter);
    }

    @Provides
    ServiceProvider provideServiceProvider(RestAdapter restAdapter, ApiKeyProvider apiKeyProvider) {
        return new ServiceProvider(restAdapter, apiKeyProvider);
    }

    @Provides
    ApiKeyProvider provideApiKeyProvider(AccountManager accountManager) {
        return new ApiKeyProvider(accountManager);
    }

    @Provides
    Gson provideGson() {
        /**
         * GSON instance to use for all request  with date format set up for proper parsing.
         * <p/>
         * You can also configure GSON with different naming policies for your API.
         * Maybe your API is Rails API and all json values are lower case with an underscore,
         * like this "first_name" instead of "firstName".
         * You can configure GSON as such below.
         * <p/>
         *
         * public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd")
         *         .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
         */
        return new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    }

    @Provides
    RestErrorHandler provideRestErrorHandler(Bus bus) {
        return new RestErrorHandler(bus);
    }

    @Provides
    RestAdapterRequestInterceptor provideRestAdapterRequestInterceptor(UserAgentProvider userAgentProvider, ApiKeyProvider apiKeyProvider) {
        return new RestAdapterRequestInterceptor(userAgentProvider, apiKeyProvider);
    }

    @Provides
    RestAdapter provideRestAdapter(RestErrorHandler restErrorHandler, RestAdapterRequestInterceptor restRequestInterceptor, Gson gson) {
        return new RestAdapter.Builder()
                .setEndpoint(Constants.Http.URL_BASE)
                .setErrorHandler(restErrorHandler)
                .setRequestInterceptor(restRequestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .build();
    }

}
