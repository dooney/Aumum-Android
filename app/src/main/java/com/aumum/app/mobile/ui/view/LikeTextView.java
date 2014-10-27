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

    private OnLikeListener likeListener;

    public void setLike(boolean isLike) {
        this.isLike = isLike;
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
        int drawableId = (isLike ? R.drawable.ic_fa_thumbs_up : R.drawable.ic_fa_thumbs_o_up);
        setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
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
            } else {
                setText(getResources().getString(R.string.label_like));
            }
        } catch (NumberFormatException e) {
            setText("1");
        }
    }
}
