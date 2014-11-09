package com.aumum.app.mobile.ui.party;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.helper.MessageBuilder;
import com.aumum.app.mobile.core.service.MessageDeliveryService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 10/10/2014.
 */
public class LikeListener implements LikeTextView.OnLikeListener {
    private SafeAsyncTask<Boolean> task;

    private Party party;

    @Inject RestService service;
    @Inject
    MessageDeliveryService messageDeliveryService;

    private UserStore userStore;

    private LikeFinishedListener likeFinishedListener;

    public void setOnLikeFinishedListener(LikeFinishedListener likeFinishedListener) {
        this.likeFinishedListener = likeFinishedListener;
    }

    public static interface LikeFinishedListener {
        public void OnLikeFinished(Party party);
        public void OnUnLikeFinished(Party party);
    }

    public LikeListener(Party party) {
        this.party = party;
        userStore = UserStore.getInstance(null);
        Injector.inject(this);
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
            public void onSuccess(final Boolean success) {
                if (success) {
                    if (likeFinishedListener != null) {
                        likeFinishedListener.OnUnLikeFinished(party);
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

                Message message = MessageBuilder.buildPartyMessage(Message.Type.PARTY_LIKE,
                        currentUser, party.getUserId(), null, party);
                messageDeliveryService.send(message);

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
            public void onSuccess(final Boolean success) {
                if (success) {
                    if (likeFinishedListener != null) {
                        likeFinishedListener.OnLikeFinished(party);
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
