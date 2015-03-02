package com.aumum.app.mobile.ui.moment;

import android.content.Context;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 3/03/2015.
 */
public class MomentLikeListener implements LikeTextView.OnLikeListener {
    private SafeAsyncTask<Boolean> task;

    private Moment moment;

    @Inject RestService service;
    @Inject UserStore userStore;
    @Inject MomentStore momentStore;
    @Inject ChatService chatService;

    private LikeFinishedListener likeFinishedListener;

    public void setOnLikeFinishedListener(LikeFinishedListener likeFinishedListener) {
        this.likeFinishedListener = likeFinishedListener;
    }

    public static interface LikeFinishedListener {
        public void OnLikeFinished(Moment moment);
        public void OnUnLikeFinished(Moment moment);
    }

    public MomentLikeListener(Moment moment) {
        this.moment = moment;
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
                service.removeMomentLike(moment.getObjectId(), currentUser.getObjectId());
                moment.removeLike(currentUser.getObjectId());
                momentStore.save(moment);
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
                    likeFinishedListener.OnUnLikeFinished(moment);
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
                service.addMomentLike(moment.getObjectId(), currentUser.getObjectId());
                moment.addLike(currentUser.getObjectId());
                momentStore.save(moment);
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
                    likeFinishedListener.OnLikeFinished(moment);
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
        if (!moment.getUserId().equals(currentUser.getObjectId())) {
            String title = context.getString(R.string.label_like_moment_message,
                    currentUser.getScreenName());
            CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.MOMENT_LIKE,
                    title, moment.getDetails(), moment.getObjectId());
            User momentOwner = userStore.getUserById(moment.getUserId());
            chatService.sendCmdMessage(momentOwner.getChatId(), cmdMessage, false, null);
        }
    }
}
