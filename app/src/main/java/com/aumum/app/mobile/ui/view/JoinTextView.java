package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 29/10/2014.
 */
public class JoinTextView extends AnimateTextView {
    private boolean isMember;

    public boolean isMember() {
        return isMember;
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
    public void update(boolean isMember) {
        if (isMember) {
            setText(R.string.label_quit_party);
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fa_times, 0, 0, 0);
        } else {
            setText(R.string.label_join_party);
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fa_join, 0, 0, 0);
        }
        this.isMember = isMember;
    }
}
