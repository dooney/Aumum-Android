package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 10/10/2014.
 */
public class LikeTextView extends AnimateTextView {

    private boolean isLike;

    private LikeListener likeListener;

    public void setLikeListener(LikeListener likeListener) {
        this.likeListener = likeListener;
    }

    public static interface LikeListener {
        public void onUnLike(LikeTextView view);
        public void onLike(LikeTextView view);
    }

    public LikeTextView(Context context) {
        super(context);
    }

    public LikeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LikeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(boolean isLike) {
        this.isLike = isLike;
        toggleLike(isLike);
    }

    @Override
    public void onClick(View view) {
        boolean oldValue = isLike;
        update(!isLike);

        // animation
        super.onClick(view);

        if (likeListener != null) {
            if (oldValue) {
                likeListener.onUnLike(LikeTextView.this);
            } else {
                likeListener.onLike(LikeTextView.this);
            }
        }
    }

    @Override
    public void update(boolean newValue) {
        isLike = newValue;
        toggleLike(isLike);
    }

    private void toggleLike(boolean isLike) {
        if (isLike) {
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fa_liked, 0, 0, 0);
            setTextColor(getResources().getColor(R.color.am_white));
            setBackgroundResource(R.drawable.buttontext_background_dark);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fa_like, 0, 0, 0);
            setTextColor(getResources().getColor(R.color.text_light));
            setBackgroundResource(R.drawable.buttontext_background);
        }
    }
}
