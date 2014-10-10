package com.aumum.app.mobile.ui;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.BootstrapService;
import com.aumum.app.mobile.core.User;
import com.aumum.app.mobile.core.UserStore;
import com.aumum.app.mobile.events.FollowEvent;
import com.aumum.app.mobile.ui.view.FollowTextView;
import com.aumum.app.mobile.util.Ln;
import com.aumum.app.mobile.util.SafeAsyncTask;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 4/10/2014.
 */
public class FollowListener implements FollowTextView.OnFollowListener {
    private SafeAsyncTask<Boolean> task;

    private String followedUserId;

    @Inject BootstrapService service;
    @Inject Bus bus;

    private UserStore userStore;

    public FollowListener(String userId) {
        this.followedUserId = userId;
        userStore = UserStore.getInstance(null);
        Injector.inject(this);
        bus.register(this);
    }

    @Override
    public void onFollow(FollowTextView view) {
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                User followedUser = userStore.getUserById(followedUserId);
                service.addFollower(followedUserId, currentUser.getObjectId());
                service.addFollowing(currentUser.getObjectId(), followedUserId);
                currentUser.getFollowings().add(followedUserId);
                followedUser.getFollowers().add(currentUser.getObjectId());
                userStore.saveCurrentUser(currentUser);
                userStore.saveUser(followedUser);

                bus.post(new FollowEvent(followedUserId, currentUser.getObjectId()));

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
                task = null;
            }
        };
        task.execute();
    }

    @Override
    public void onUnFollow(FollowTextView view) {
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                User followedUser = userStore.getUserById(followedUserId);
                service.removeFollower(followedUserId, currentUser.getObjectId());
                service.removeFollowing(currentUser.getObjectId(), followedUserId);
                currentUser.getFollowings().remove(followedUserId);
                followedUser.getFollowers().remove(currentUser.getObjectId());
                userStore.saveCurrentUser(currentUser);
                userStore.saveUser(followedUser);
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
                task = null;
            }
        };
        task.execute();
    }
}
