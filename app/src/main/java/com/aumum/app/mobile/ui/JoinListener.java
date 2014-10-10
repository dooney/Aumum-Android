package com.aumum.app.mobile.ui;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.BootstrapService;
import com.aumum.app.mobile.core.Party;
import com.aumum.app.mobile.core.User;
import com.aumum.app.mobile.core.UserStore;
import com.aumum.app.mobile.events.JoinEvent;
import com.aumum.app.mobile.events.UnJoinEvent;
import com.aumum.app.mobile.ui.view.JoinTextView;
import com.aumum.app.mobile.util.Ln;
import com.aumum.app.mobile.util.SafeAsyncTask;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 10/10/2014.
 */
public class JoinListener implements JoinTextView.OnJoinListener {
    private SafeAsyncTask<Boolean> joinTask;

    private Party party;

    @Inject BootstrapService service;
    @Inject Bus bus;

    private UserStore userStore;

    public JoinListener(Party party) {
        this.party = party;
        userStore = UserStore.getInstance(null);
        Injector.inject(this);
        bus.register(this);
    }

    @Override
    public void onUnJoin(JoinTextView view) {
        joinTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                service.removePartyMember(party.getObjectId(), currentUser.getObjectId());
                service.removeUserParty(currentUser.getObjectId(), party.getObjectId());
                currentUser.getParties().remove(party.getObjectId());
                party.getMembers().remove(currentUser.getObjectId());
                userStore.saveCurrentUser(currentUser);

                bus.post(new UnJoinEvent(party.getUserId(), currentUser.getObjectId()));

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
                joinTask = null;
            }
        };
        joinTask.execute();
    }

    @Override
    public void onJoin(JoinTextView view) {
        joinTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                service.addPartyMember(party.getObjectId(), currentUser.getObjectId());
                service.addUserParty(currentUser.getObjectId(), party.getObjectId());
                currentUser.getParties().add(party.getObjectId());
                party.getMembers().add(currentUser.getObjectId());
                userStore.saveCurrentUser(currentUser);

                bus.post(new JoinEvent(party.getUserId(), currentUser.getObjectId()));

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
                joinTask = null;
            }
        };
        joinTask.execute();
    }
}
