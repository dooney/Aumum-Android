package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 29/10/2014.
 */
public class JoinTextView extends AnimateTextView {
    private Context context;
    private boolean isMember;

    public boolean isMember() {
        return isMember;
    }

    public JoinTextView(Context context) {
        super(context);
        this.context = context;
    }

    public JoinTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public JoinTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    public void update(boolean isMember) {
        if (isMember) {
            setText(R.string.label_quit_party);
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fa_times, 0, 0, 0);
        } else {
            setText(R.string.label_join);
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fa_join, 0, 0, 0);
        }
        this.isMember = isMember;
    }
}
