package com.aumum.app.mobile;

import android.accounts.AccountManager;
import android.content.Context;

import com.aumum.app.mobile.core.dao.CreditRuleStore;
import com.aumum.app.mobile.core.dao.Repository;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.core.service.NotificationService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.LaunchActivity;
import com.aumum.app.mobile.ui.account.CompleteProfileActivity;
import com.aumum.app.mobile.ui.account.ResetPasswordSuccessActivity;
import com.aumum.app.mobile.ui.area.AreaListFragment;
import com.aumum.app.mobile.ui.chat.ChatConnectionListener;
import com.aumum.app.mobile.ui.chat.ChatFragment;
import com.aumum.app.mobile.ui.chat.ChatTabFragment;
import com.aumum.app.mobile.ui.group.GroupChangeListener;
import com.aumum.app.mobile.ui.group.GroupDetailsFragment;
import com.aumum.app.mobile.ui.chat.ImageMessageCard;
import com.aumum.app.mobile.ui.chat.MessageNotifyListener;
import com.aumum.app.mobile.ui.chat.NotificationClickListener;
import com.aumum.app.mobile.ui.chat.SendMessageListener;
import com.aumum.app.mobile.ui.chat.TextMessageCard;
import com.aumum.app.mobile.ui.chat.VoiceMessageCard;
import com.aumum.app.mobile.ui.contact.ContactFragment;
import com.aumum.app.mobile.ui.group.GroupFragment;
import com.aumum.app.mobile.ui.group.GroupJoinListener;
import com.aumum.app.mobile.ui.group.GroupRequestProcessListener;
import com.aumum.app.mobile.ui.group.GroupRequestsFragment;
import com.aumum.app.mobile.ui.group.GroupListFragment;
import com.aumum.app.mobile.ui.group.NewGroupActivity;
import com.aumum.app.mobile.ui.contact.ContactPickerFragment;
import com.aumum.app.mobile.ui.contact.MobileContactsActivity;
import com.aumum.app.mobile.ui.conversation.ConversationFragment;
import com.aumum.app.mobile.ui.contact.AcceptContactListener;
import com.aumum.app.mobile.ui.contact.ContactListener;
import com.aumum.app.mobile.ui.contact.ContactRequestsFragment;
import com.aumum.app.mobile.ui.account.VerifyActivity;
import com.aumum.app.mobile.ui.report.ReportActivity;
import com.aumum.app.mobile.ui.settings.FeedbackActivity;
import com.aumum.app.mobile.ui.settings.NotificationActivity;
import com.aumum.app.mobile.ui.user.AreaUsersFragment;
import com.aumum.app.mobile.ui.user.TagUsersFragment;
import com.aumum.app.mobile.ui.user.UpdateAvatarActivity;
import com.aumum.app.mobile.ui.main.MainFragment;
import com.aumum.app.mobile.ui.like.LikesLayoutListener;
import com.aumum.app.mobile.ui.account.ResetPasswordActivity;
import com.aumum.app.mobile.ui.account.LoginActivity;
import com.aumum.app.mobile.core.service.LogoutService;
import com.aumum.app.mobile.ui.account.RegisterActivity;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.ui.settings.SettingsActivity;
import com.aumum.app.mobile.ui.user.ProfileFragment;
import com.aumum.app.mobile.ui.user.UserListFragment;
import com.aumum.app.mobile.ui.user.UserTagListFragment;
import com.aumum.app.mobile.utils.PostFromAnyThreadBus;
import com.aumum.app.mobile.core.api.RestAdapterRequestInterceptor;
import com.aumum.app.mobile.core.api.RestErrorHandler;
import com.aumum.app.mobile.core.api.UserAgentProvider;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.ui.main.MainActivity;
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
                LaunchActivity.class,
                LoginActivity.class,
                RegisterActivity.class,
                ResetPasswordActivity.class,
                MainActivity.class,
                com.aumum.app.mobile.ui.user.UserFragment.class,
                ConversationFragment.class,
                ChatFragment.class,
                SendMessageListener.class,
                LikesLayoutListener.class,
                MainFragment.class,
                ContactListener.class,
                ContactRequestsFragment.class,
                AcceptContactListener.class,
                ContactFragment.class,
                TextMessageCard.class,
                ProfileFragment.class,
                SettingsActivity.class,
                VoiceMessageCard.class,
                ImageMessageCard.class,
                UpdateAvatarActivity.class,
                VerifyActivity.class,
                CompleteProfileActivity.class,
                ResetPasswordSuccessActivity.class,
                MessageNotifyListener.class,
                NotificationClickListener.class,
                MobileContactsActivity.class,
                GroupDetailsFragment.class,
                ContactPickerFragment.class,
                UserListFragment.class,
                ReportActivity.class,
                ChatConnectionListener.class,
                FeedbackActivity.class,
                AreaListFragment.class,
                AreaUsersFragment.class,
                NotificationActivity.class,
                UserTagListFragment.class,
                TagUsersFragment.class,
                GroupListFragment.class,
                GroupChangeListener.class,
                GroupRequestsFragment.class,
                GroupJoinListener.class,
                GroupRequestProcessListener.class,
                ChatTabFragment.class,
                GroupFragment.class,
                NewGroupActivity.class
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
    NotificationService provideNotificationService(final Context context) {
        return new NotificationService(context);
    }

    @Provides
    @Singleton
    LogoutService provideLogoutService(final Context context, final AccountManager accountManager) {
        return new LogoutService(context, accountManager);
    }

    @Provides
    @Singleton
    ChatService provideChatService() { return new ChatService(); }

    @Provides
    @Singleton
    FileUploadService provideFileUploadService() { return new FileUploadService(); }

    @Provides
    @Singleton
    Repository provideRepository(final Context context) {
        return new Repository(context);
    }

    @Provides
    RestService provideRestService(RestAdapter restAdapter) {
        return new RestService(restAdapter);
    }

    @Provides
    @Singleton
    UserStore provideUserStore(RestService restService, ApiKeyProvider apiKeyProvider, Repository repository) {
        return new UserStore(restService, apiKeyProvider, repository);
    }

    @Provides
    @Singleton
    CreditRuleStore provideCreditRuleStore(RestService restService, Repository repository) {
        return new CreditRuleStore(restService, repository);
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
