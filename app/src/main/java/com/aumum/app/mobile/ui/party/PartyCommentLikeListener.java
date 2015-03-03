package com.aumum.app.mobile.ui.party;

import android.content.Context;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.PartyComment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 7/01/2015.
 */
public class PartyCommentLikeListener implements LikeTextView.OnLikeListener {

    @Inject UserStore userStore;
    @Inject RestService service;
    @Inject ChatService chatService;

    private PartyComment comment;
    private SafeAsyncTask<Boolean> task;

    public PartyCommentLikeListener(PartyComment comment) {
        this.comment = comment;
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
                service.removePartyCommentLike(comment.getObjectId(), currentUser.getObjectId());
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
    public void onLike(final LikeTextView view) {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                service.addPartyCommentLike(comment.getObjectId(), currentUser.getObjectId());
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
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }

    private void sendLikeMessage(Context context, User currentUser) throws Exception {
        if (!comment.isOwner(currentUser.getObjectId())) {
            String title = context.getString(R.string.label_like_party_comment_message,
                    currentUser.getScreenName());
            CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.PARTY_COMMENT_LIKE,
                    title, comment.getContent(), comment.getParentId());
            User commentOwner = userStore.getUserById(comment.getUserId());
            chatService.sendCmdMessage(commentOwner.getChatId(), cmdMessage, false, null);
        }
    }
}
