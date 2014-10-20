package com.aumum.app.mobile.ui.party;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.events.MessageEvent;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 10/10/2014.
 */
public class LikeListener implements LikeTextView.OnLikeListener {
    private SafeAsyncTask<Boolean> task;

    private Party party;

    @Inject
    RestService service;
    @Inject Bus bus;

    private UserStore userStore;

    public LikeListener(Party party) {
        this.party = party;
        userStore = UserStore.getInstance(null);
        Injector.inject(this);
        bus.register(this);
    }

    @Override
    public void onUnLike(LikeTextView view) {
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser(false);
                service.removePartyFan(party.getObjectId(), currentUser.getObjectId());
                party.getFans().remove(currentUser.getObjectId());
                userStore.saveUser(currentUser);

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
    public void onLike(LikeTextView view) {
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser(false);
                service.addPartyFan(party.getObjectId(), currentUser.getObjectId());
                party.getFans().add(currentUser.getObjectId());
                userStore.saveUser(currentUser);

                bus.post(new MessageEvent(Message.LIKE, party.getUserId(), currentUser.getObjectId()));

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
