package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 3/10/2014.
 */
public class FollowTextView extends AnimateTextView {

    private boolean isFollowing;

    private OnFollowListener followListener;

    public void setFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public void setFollowListener(OnFollowListener followListener) {
        this.followListener = followListener;
    }

    public static interface OnFollowListener {
        public void onUnFollow(FollowTextView view);
        public void onFollow(FollowTextView view);
    }

    public FollowTextView(Context context) {
        super(context);
    }

    public FollowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FollowTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onClick(View view) {
        boolean oldValue = isFollowing;
        update(!isFollowing);

        // animation
        super.onClick(view);

        if (followListener != null) {
            if (oldValue) {
                followListener.onUnFollow(FollowTextView.this);
            } else {
                followListener.onFollow(FollowTextView.this);
            }
        }
    }

    @Override
    public void update(boolean newValue) {
        isFollowing = newValue;
        int drawableId = (isFollowing ? R.drawable.ic_fa_check_circle : R.drawable.ic_fa_plus_circle);
        setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
        setText(isFollowing ? R.string.label_unfollow : R.string.label_follow);
    }
}
