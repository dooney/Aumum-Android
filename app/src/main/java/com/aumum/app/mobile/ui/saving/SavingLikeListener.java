package com.aumum.app.mobile.ui.saving;

import android.content.Context;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.SavingStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.Saving;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 12/03/2015.
 */
public class SavingLikeListener implements LikeTextView.OnLikeListener {
    private SafeAsyncTask<Boolean> task;

    private Saving saving;

    @Inject RestService service;
    @Inject UserStore userStore;
    @Inject SavingStore savingStore;
    @Inject ChatService chatService;

    private LikeFinishedListener likeFinishedListener;

    public void setOnLikeFinishedListener(LikeFinishedListener likeFinishedListener) {
        this.likeFinishedListener = likeFinishedListener;
    }

    public static interface LikeFinishedListener {
        public void OnLikeFinished(Saving saving);
        public void OnUnLikeFinished(Saving saving);
    }

    public SavingLikeListener(Saving saving) {
        this.saving = saving;
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
                service.removeSavingLike(saving.getObjectId(), currentUser.getObjectId());
                saving.removeLike(currentUser.getObjectId());
                savingStore.save(saving);
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
                    likeFinishedListener.OnUnLikeFinished(saving);
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
                service.addSavingLike(saving.getObjectId(), currentUser.getObjectId());
                saving.addLike(currentUser.getObjectId());
                savingStore.save(saving);
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
                    likeFinishedListener.OnLikeFinished(saving);
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
        if (!saving.getUserId().equals(currentUser.getObjectId())) {
            String title = context.getString(R.string.label_like_saving_message,
                    currentUser.getScreenName());
            CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.SAVING_LIKE,
                    title, saving.getDetails(), saving.getObjectId());
            User savingOwner = userStore.getUserById(saving.getUserId());
            chatService.sendCmdMessage(savingOwner.getChatId(), cmdMessage, false, null);
        }
    }
}
