package com.aumum.app.mobile.ui.asking;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.AskingReply;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 2/12/2014.
 */
public class AskingReplyLikeListener implements LikeTextView.OnLikeListener {

    private SafeAsyncTask<Boolean> task;

    private AskingReply askingReply;

    @Inject RestService service;
    @Inject ApiKeyProvider apiKeyProvider;

    public AskingReplyLikeListener(AskingReply askingReply) {
        this.askingReply = askingReply;
        Injector.inject(this);
    }

    @Override
    public void onUnLike(LikeTextView view) {
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                String currentUserId = apiKeyProvider.getAuthUserId();
                service.removeAskingReplyLike(askingReply.getObjectId(), currentUserId);
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
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                String currentUserId = apiKeyProvider.getAuthUserId();
                service.addAskingReplyLike(askingReply.getObjectId(), currentUserId);
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
