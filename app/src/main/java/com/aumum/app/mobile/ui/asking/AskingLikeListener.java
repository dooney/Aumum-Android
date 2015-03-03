package com.aumum.app.mobile.ui.asking;

import android.content.Context;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 3/01/2015.
 */
public class AskingLikeListener implements LikeTextView.OnLikeListener {

    @Inject RestService service;
    @Inject ChatService chatService;
    @Inject UserStore userStore;

    private Asking asking;
    private SafeAsyncTask<Boolean> task;

    public AskingLikeListener(Asking asking) {
        this.asking = asking;
        Injector.inject(this);
    }

    @Override
    public void onUnLike(LikeTextView view) {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User user = userStore.getCurrentUser();
                service.removeAskingLike(asking.getObjectId(), user.getObjectId());
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
                User user = userStore.getCurrentUser();
                service.addAskingLike(asking.getObjectId(), user.getObjectId());
                sendLikeMessage(view.getContext(), user);
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
        if (!asking.isOwner(currentUser.getObjectId())) {
            String title = context.getString(R.string.label_like_asking_message,
                    currentUser.getScreenName());
            CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.ASKING_LIKE,
                    title, asking.getTitle(), asking.getObjectId());
            User askingOwner = userStore.getUserById(asking.getUserId());
            chatService.sendCmdMessage(askingOwner.getChatId(), cmdMessage, false, null);
        }
    }
}
