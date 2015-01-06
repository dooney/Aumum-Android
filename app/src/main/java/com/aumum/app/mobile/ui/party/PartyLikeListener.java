package com.aumum.app.mobile.ui.party;

import android.content.Context;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 10/10/2014.
 */
public class PartyLikeListener implements LikeTextView.OnLikeListener {
    private SafeAsyncTask<Boolean> task;

    private Party party;

    @Inject RestService service;
    @Inject UserStore userStore;
    @Inject PartyStore partyStore;
    @Inject ChatService chatService;

    private LikeFinishedListener likeFinishedListener;

    public void setOnLikeFinishedListener(LikeFinishedListener likeFinishedListener) {
        this.likeFinishedListener = likeFinishedListener;
    }

    public static interface LikeFinishedListener {
        public void OnLikeFinished(Party party);
        public void OnUnLikeFinished(Party party);
    }

    public PartyLikeListener(Party party) {
        this.party = party;
        Injector.inject(this);
    }

    @Override
    public void onUnLike(LikeTextView view) {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                service.removePartyLike(party.getObjectId(), currentUser.getObjectId());
                party.removeLike(currentUser.getObjectId());
                partyStore.save(party);
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
                if (likeFinishedListener != null) {
                    likeFinishedListener.OnUnLikeFinished(party);
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
    public void onLike(final LikeTextView view) {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                service.addPartyLike(party.getObjectId(), currentUser.getObjectId());
                party.addLike(currentUser.getObjectId());
                partyStore.save(party);
                sendLikeMessage(view.getContext(), currentUser);
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
                if (likeFinishedListener != null) {
                    likeFinishedListener.OnLikeFinished(party);
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }

    private void sendLikeMessage(Context context, User currentUser) throws Exception {
        if (!party.getUserId().equals(currentUser.getObjectId())) {
            String title = context.getString(R.string.label_like_party_message,
                    currentUser.getScreenName());
            CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.PARTY_LIKE,
                    title, party.getTitle(), party.getObjectId());
            User partyOwner = userStore.getUserById(party.getUserId());
            chatService.sendCmdMessage(partyOwner.getChatId(), cmdMessage, false, null);
        }
    }
}
