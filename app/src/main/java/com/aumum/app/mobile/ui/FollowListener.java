package com.aumum.app.mobile.ui;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.authenticator.ApiKeyProvider;
import com.aumum.app.mobile.core.BootstrapService;
import com.aumum.app.mobile.ui.view.FollowTextView;
import com.aumum.app.mobile.util.Ln;
import com.aumum.app.mobile.util.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 4/10/2014.
 */
public class FollowListener implements FollowTextView.OnFollowListener {
    private SafeAsyncTask<Boolean> followTask;
    private SafeAsyncTask<Boolean> unFollowTask;

    private String followedUserId;

    @Inject BootstrapService service;
    @Inject ApiKeyProvider apiKeyProvider;

    public FollowListener(String userId) {
        this.followedUserId = userId;
        Injector.inject(this);
    }

    @Override
    public void onFollow(FollowTextView view) {
        followTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                String currentUserId = apiKeyProvider.getAuthUserId();
                service.addFollower(followedUserId, currentUserId);
                service.addFollowing(currentUserId, followedUserId);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                // Retrofit Errors are handled inside of the {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                followTask = null;
            }
        };
        followTask.execute();
    }

    @Override
    public void onUnFollow(FollowTextView view) {
        unFollowTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                // Retrofit Errors are handled inside of the {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                unFollowTask = null;
            }
        };
        unFollowTask.execute();
    }
}
