package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 3/10/2014.
 */
public abstract class AnimateTextView extends TextView implements View.OnClickListener {
    public AnimateTextView(Context context) {
        super(context);
        setOnClickListener(this);
    }

    public AnimateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    public AnimateTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Animation.animateTextView(this);
    }

    public abstract void update(boolean newValue);
}
