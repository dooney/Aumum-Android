package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 29/10/2014.
 */
public class JoinTextView extends AnimateTextView {
    private Context context;

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
            setTypeface(null, Typeface.NORMAL);
            setTextColor(context.getResources().getColor(R.color.text_warning));
        } else {
            setText(R.string.label_join);
            setTypeface(null, Typeface.BOLD);
            setTextColor(context.getResources().getColor(R.color.text_link));
        }
    }
}
