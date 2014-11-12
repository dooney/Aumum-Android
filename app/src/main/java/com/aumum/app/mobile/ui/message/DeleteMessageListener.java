package com.aumum.app.mobile.ui.message;

import android.view.View;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.dao.gen.MessageVM;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 23/10/2014.
 */
public class DeleteMessageListener implements View.OnClickListener {
    private MessageVM message;
    private String currentUserId;

    private SafeAsyncTask<Boolean> task;

    @Inject RestService service;

    protected OnActionListener onActionListener;

    protected OnProgressListener onProgressListener;

    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public static interface OnActionListener {
        public void onMessageDeletedSuccess(String messageId);
    }

    public static interface OnProgressListener {
        public void onDeleteMessageStart();
        public void onDeleteMessageFinish();
    }

    public DeleteMessageListener(MessageVM message, String currentUserId) {
        this.message = message;
        this.currentUserId = currentUserId;
        Injector.inject(this);
    }

    @Override
    public void onClick(View view) {
        deleteMessage();
    }

    private void deleteMessage() {
        if (onProgressListener != null) {
            onProgressListener.onDeleteMessageStart();
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                service.deleteMessage(message.getObjectId());
                service.removeUserMessage(currentUserId, message.getObjectId());
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
                if (onActionListener != null) {
                    onActionListener.onMessageDeletedSuccess(message.getObjectId());
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                if (onProgressListener != null) {
                    onProgressListener.onDeleteMessageFinish();
                }
                task = null;
            }
        };
        task.execute();
    }
}
