package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 10/10/2014.
 */
public class CommentTextView extends IconTextView {
    private OnCommentListener commentListener;

    public void setCommentListener(OnCommentListener commentListener) {
        this.commentListener = commentListener;
    }

    public static interface OnCommentListener {
        public void onComment(CommentTextView view);
    }

    public CommentTextView(Context context) {
        super(context);
    }

    public CommentTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (commentListener != null) {
            commentListener.onComment(CommentTextView.this);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void update(boolean newValue) {

    }
}
