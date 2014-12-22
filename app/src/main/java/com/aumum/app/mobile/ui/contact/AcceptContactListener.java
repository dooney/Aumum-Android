package com.aumum.app.mobile.ui.contact;

import android.content.Context;
import android.view.View;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 20/11/2014.
 */
public class AcceptContactListener implements View.OnClickListener {
    @Inject ChatService chatService;
    @Inject RestService restService;
    @Inject ApiKeyProvider apiKeyProvider;
    @Inject UserStore userStore;

    private String userId;
    private SafeAsyncTask<Boolean> task;

    protected OnActionListener onActionListener;

    protected OnProgressListener onProgressListener;

    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public static interface OnActionListener {
        public void onAcceptContactSuccess();
        public void onAcceptContactFailed();
    }

    public static interface OnProgressListener {
        public void onAcceptContactStart();
        public void onAcceptContactFinish();
    }

    public AcceptContactListener(String userId) {
        Injector.inject(this);
        this.userId = userId;
    }

    @Override
    public void onClick(View view) {
        acceptContact(view.getContext());
    }

    private void acceptContact(final Context context) {
        if (task != null) {
            return;
        }
        if (onProgressListener != null) {
            onProgressListener.onAcceptContactStart();
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                chatService.acceptInvitation(userId);
                String currentUserId = apiKeyProvider.getAuthUserId();
                restService.addContact(currentUserId, userId);
                userStore.addContact(currentUserId, userId);
                restService.addContact(userId, currentUserId);
                userStore.addContact(userId, currentUserId);

                String text = context.getString(R.string.info_invitation_accepted_and_start_chatting);
                User user = userStore.getUserById(userId);
                chatService.sendSystemMessage(user.getChatId(), false, text, null);
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
                if (onActionListener != null) {
                    onActionListener.onAcceptContactFailed();
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                if (onActionListener != null) {
                    onActionListener.onAcceptContactSuccess();
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                if (onProgressListener != null) {
                    onProgressListener.onAcceptContactFinish();
                }
                task = null;
            }
        };
        task.execute();
    }
}
