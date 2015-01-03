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
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fa_join, 0, 0, 0);
            setText(R.string.label_quit_party);
            setTextColor(getResources().getColor(R.color.bbutton_danger));
        } else {
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fa_join_o, 0, 0, 0);
            setText(R.string.label_join_party);
            setTextColor(getResources().getColor(R.color.text_light));
        }
        this.isMember = isMember;
    }
}
