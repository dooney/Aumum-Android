package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 3/10/2014.
 */
public class FollowTextView extends IconTextView{

    private boolean isFollowing;

    private OnFollowListener followListener;

    public void setFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public OnFollowListener getFollowListener() {
        return followListener;
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
    public boolean onTouchEvent(MotionEvent event) {
        boolean oldValue = isFollowing;
        update(!isFollowing);

        // animation
        boolean ret = super.onTouchEvent(event);

        if (oldValue) {
            getFollowListener().onUnFollow(FollowTextView.this);
        } else {
            getFollowListener().onFollow(FollowTextView.this);
        }

        return ret;
    }

    private void update(boolean newValue) {
        isFollowing = newValue;
        int drawableId = (isFollowing ? R.drawable.ic_fa_check_circle : R.drawable.ic_fa_plus_circle);
        setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
        setText(isFollowing ? R.string.label_unfollow : R.string.label_follow);
    }
}
