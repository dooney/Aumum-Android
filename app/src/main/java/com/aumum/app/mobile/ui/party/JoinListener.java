package com.aumum.app.mobile.ui.party;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.RestService;
import com.aumum.app.mobile.core.Party;
import com.aumum.app.mobile.core.User;
import com.aumum.app.mobile.core.UserStore;
import com.aumum.app.mobile.events.JoinEvent;
import com.aumum.app.mobile.events.UnJoinEvent;
import com.aumum.app.mobile.ui.view.JoinTextView;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 10/10/2014.
 */
public class JoinListener implements JoinTextView.OnJoinListener {
    private SafeAsyncTask<Boolean> task;

    private Party party;

    @Inject
    RestService service;
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
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser(false);
                service.removePartyMember(party.getObjectId(), currentUser.getObjectId());
                service.removeUserParty(currentUser.getObjectId(), party.getObjectId());
                currentUser.getParties().remove(party.getObjectId());
                party.getMembers().remove(currentUser.getObjectId());
                userStore.saveUser(currentUser);

                if (party.getUserId() != currentUser.getObjectId()) {
                    bus.post(new UnJoinEvent(party.getUserId(), currentUser.getObjectId()));
                }

                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
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
    public void onJoin(JoinTextView view) {
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser(false);
                service.addPartyMember(party.getObjectId(), currentUser.getObjectId());
                service.addUserParty(currentUser.getObjectId(), party.getObjectId());
                currentUser.getParties().add(party.getObjectId());
                party.getMembers().add(currentUser.getObjectId());
                userStore.saveUser(currentUser);

                if (party.getUserId() != currentUser.getObjectId()) {
                    bus.post(new JoinEvent(party.getUserId(), currentUser.getObjectId()));
                }

                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
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
