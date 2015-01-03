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
    private int textResId;
    private int likeResId;
    private int likedResId;

    private OnLikeListener likeListener;

    public void setTextResId(int textResId) {
        this.textResId = textResId;
    }

    public void setLikeResId(int likeResId) {
        this.likeResId = likeResId;
    }

    public void setLikedResId(int likedResId) {
        this.likedResId = likedResId;
    }

    public void setLikeListener(OnLikeListener likeListener) {
        this.likeListener = likeListener;
    }

    public static interface OnLikeListener {
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

    public void init(int likes, boolean isLike) {
        if (likes > 0) {
            setText(String.valueOf(likes));
        } else if (textResId > 0) {
            setText(getResources().getString(textResId));
        } else {
            setText(null);
        }
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
        String currentText = getText().toString();
        try {
            Integer currentLikes = Integer.parseInt(currentText);
            if (isLike) {
                currentLikes++;
            } else {
                currentLikes--;
            }
            if (currentLikes > 0) {
                setText(currentLikes.toString());
            } else if (textResId > 0) {
                setText(getResources().getString(textResId));
            } else {
                setText(null);
            }
        } catch (NumberFormatException e) {
            setText("1");
        }
    }

    private void toggleLike(boolean isLike) {
        if (isLike) {
            setCompoundDrawablesWithIntrinsicBounds(likedResId, 0, 0, 0);
            setTextColor(getResources().getColor(R.color.bbutton_danger));
        } else {
            setCompoundDrawablesWithIntrinsicBounds(likeResId, 0, 0, 0);
            setTextColor(getResources().getColor(R.color.text_light));
        }
    }
}
