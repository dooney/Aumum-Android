package com.aumum.app.mobile;

import android.accounts.AccountManager;
import android.content.Context;

import com.aumum.app.mobile.core.dao.PartyReasonStore;
import com.aumum.app.mobile.core.dao.Repository;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.MessageDeliveryService;
import com.aumum.app.mobile.core.service.NotificationService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.LaunchActivity;
import com.aumum.app.mobile.ui.chat.ChatFragment;
import com.aumum.app.mobile.ui.chat.SendMessageListener;
import com.aumum.app.mobile.ui.chat.TextMessageCard;
import com.aumum.app.mobile.ui.contact.MyGroupsFragment;
import com.aumum.app.mobile.ui.conversation.ConversationFragment;
import com.aumum.app.mobile.ui.contact.AllGroupsFragment;
import com.aumum.app.mobile.ui.contact.GroupJoinListener;
import com.aumum.app.mobile.ui.contact.GroupQuitListener;
import com.aumum.app.mobile.ui.comment.DeleteCommentListener;
import com.aumum.app.mobile.ui.contact.AcceptContactListener;
import com.aumum.app.mobile.ui.contact.ContactFragment;
import com.aumum.app.mobile.ui.contact.ContactListener;
import com.aumum.app.mobile.ui.contact.ContactRequestsFragment;
import com.aumum.app.mobile.ui.contact.DeleteContactListener;
import com.aumum.app.mobile.ui.main.MainFragment;
import com.aumum.app.mobile.ui.party.LikesLayoutListener;
import com.aumum.app.mobile.ui.party.MembersLayoutListener;
import com.aumum.app.mobile.ui.party.PartyListFragment;
import com.aumum.app.mobile.ui.party.PartyReasonsFragment;
import com.aumum.app.mobile.ui.party.NewPartyActivity;
import com.aumum.app.mobile.ui.party.PartyActionListener;
import com.aumum.app.mobile.ui.party.PartyOwnerActionListener;
import com.aumum.app.mobile.ui.account.ResetPasswordActivity;
import com.aumum.app.mobile.ui.login.LoginActivity;
import com.aumum.app.mobile.core.service.LogoutService;
import com.aumum.app.mobile.ui.party.PartyUserActionListener;
import com.aumum.app.mobile.ui.party.SearchPartyFragment;
import com.aumum.app.mobile.ui.register.RegisterActivity;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.MessageStore;
import com.aumum.app.mobile.core.dao.PartyCommentStore;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.ui.contact.AddContactActivity;
import com.aumum.app.mobile.utils.PostFromAnyThreadBus;
import com.aumum.app.mobile.core.api.RestAdapterRequestInterceptor;
import com.aumum.app.mobile.core.api.RestErrorHandler;
import com.aumum.app.mobile.core.api.UserAgentProvider;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.ui.party.LikeListener;
import com.aumum.app.mobile.ui.main.MainActivity;
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
                LaunchActivity.class,
                LoginActivity.class,
                RegisterActivity.class,
                ResetPasswordActivity.class,
                MainActivity.class,
                NewPartyActivity.class,
                LikeListener.class,
                PartyCommentStore.class,
                PartyCommentsFragment.class,
                UserFragment.class,
                PartyDetailsFragment.class,
                UserProfileImageActivity.class,
                PartyActionListener.class,
                PartyOwnerActionListener.class,
                PartyUserActionListener.class,
                DeleteCommentListener.class,
                PartyReasonStore.class,
                PartyReasonsFragment.class,
                ConversationFragment.class,
                AllGroupsFragment.class,
                GroupJoinListener.class,
                GroupQuitListener.class,
                ChatFragment.class,
                SendMessageListener.class,
                PartyListFragment.class,
                SearchPartyFragment.class,
                MembersLayoutListener.class,
                LikesLayoutListener.class,
                MainFragment.class,
                AddContactActivity.class,
                ContactListener.class,
                ContactRequestsFragment.class,
                AcceptContactListener.class,
                DeleteContactListener.class,
                ContactFragment.class,
                TextMessageCard.class,
                MyGroupsFragment.class
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
    MessageDeliveryService provideMessageDeliveryService(RestService restService) {
        return new MessageDeliveryService(restService);
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
    Repository provideRepository(final Context context) {
        return new Repository(context);
    }

    @Provides
    RestService provideRestService(RestAdapter restAdapter) {
        return new RestService(restAdapter);
    }

    @Provides
    @Singleton
    MessageStore provideMessageStore(RestService restService, Repository repository) {
        return new MessageStore(restService, repository);
    }

    @Provides
    @Singleton
    UserStore provideUserStore(RestService restService, ApiKeyProvider apiKeyProvider, Repository repository) {
        return new UserStore(restService, apiKeyProvider, repository);
    }

    @Provides
    @Singleton
    PartyStore providePartyStore(RestService restService, Repository repository) {
        return new PartyStore(restService, repository);
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
