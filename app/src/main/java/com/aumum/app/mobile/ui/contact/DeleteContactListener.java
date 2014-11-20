package com.aumum.app.mobile.ui.contact;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 20/11/2014.
 */
public class DeleteContactListener implements View.OnClickListener,
        DialogInterface.OnClickListener {

    @Inject ChatService chatService;
    @Inject RestService restService;
    @Inject ApiKeyProvider apiKeyProvider;

    private Context context;
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
        public void onDeleteContactSuccess(String contactId);
        public void onDeleteContactFailed();
    }

    public static interface OnProgressListener {
        public void onDeleteContactStart();
        public void onDeleteContactFinish();
    }

    public DeleteContactListener(Context context, String userId) {
        Injector.inject(this);
        this.context = context;
        this.userId = userId;
    }

    @Override
    public void onClick(View view) {
        DialogUtils.showDialog(context,
                R.string.info_confirm_delete_contact,
                R.string.label_ok,
                this,
                R.string.label_cancel,
                null);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (onProgressListener != null) {
            onProgressListener.onDeleteContactStart();
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                chatService.deleteContact(userId);
                String currentUserId = apiKeyProvider.getAuthUserId();
                restService.removeContact(currentUserId, userId);
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
                    onActionListener.onDeleteContactFailed();
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                if (onActionListener != null) {
                    onActionListener.onDeleteContactSuccess(userId);
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                if (onProgressListener != null) {
                    onProgressListener.onDeleteContactFinish();
                }
                task = null;
            }
        };
        task.execute();
    }
}
