package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 10/10/2014.
 */
public class JoinTextView extends IconTextView {
    private boolean isJoin;

    private OnJoinListener joinListener;

    public void setJoin(boolean isJoin) {
        this.isJoin = isJoin;
    }

    public void setJoinListener(OnJoinListener joinListener) {
        this.joinListener = joinListener;
    }

    public static interface OnJoinListener {
        public void onUnJoin(JoinTextView view);
        public void onJoin(JoinTextView view);
    }

    public JoinTextView(Context context) {
        super(context);
    }

    public JoinTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JoinTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean oldValue = isJoin;
        update(!isJoin);

        // animation
        boolean ret = super.onTouchEvent(event);

        if (joinListener != null) {
            if (oldValue) {
                joinListener.onUnJoin(JoinTextView.this);
            } else {
                joinListener.onJoin(JoinTextView.this);
            }
        }

        return ret;
    }

    @Override
    public void update(boolean newValue) {
        isJoin = newValue;
        int drawableId = (isJoin ? R.drawable.ic_fa_check : R.drawable.ic_fa_users);
        setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
        String currentText = getText().toString();
        try {
            Integer currentJoins = Integer.parseInt(currentText);
            if (isJoin) {
                currentJoins++;
            } else {
                currentJoins--;
            }
            if (currentJoins > 0) {
                setText(currentJoins.toString());
            } else {
                setText(getResources().getString(R.string.label_join));
            }
        } catch (NumberFormatException e) {
            setText("1");
        }
    }
}
