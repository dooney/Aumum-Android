package com.aumum.app.mobile.ui.comment;

import android.view.View;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.model.Comment;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 22/10/2014.
 */
public class DeleteCommentListener implements View.OnClickListener {
    private Comment comment;
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
        public void onCommentDeletedSuccess(String commentId);
    }

    public static interface OnProgressListener {
        public void onDeleteCommentStart();
        public void onDeleteCommentFinish();
    }

    public DeleteCommentListener(Comment comment, String currentUserId) {
        this.comment = comment;
        this.currentUserId = currentUserId;
        Injector.inject(this);
    }

    @Override
    public void onClick(View view) {
        deleteComment();
    }

    private void deleteComment() {
        if (onProgressListener != null) {
            onProgressListener.onDeleteCommentStart();
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                service.deleteComment(comment.getObjectId());
                service.removePartyComment(comment.getParentId(), comment.getObjectId());
                service.removeUserComment(currentUserId, comment.getObjectId());
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
                    onActionListener.onCommentDeletedSuccess(comment.getObjectId());
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                if (onProgressListener != null) {
                    onProgressListener.onDeleteCommentFinish();
                }
                task = null;
            }
        };
        task.execute();
    }
}