package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 23/06/2015.
 */
public class LikeCountTextView extends LikeTextView {

    private int count;

    public LikeCountTextView(Context context) {
        super(context);
    }

    public LikeCountTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LikeCountTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(boolean isLike, int count) {
        this.count = count;
        super.init(isLike);
    }

    @Override
    public void update(boolean newValue) {
        if (newValue) {
            count++;
        } else {
            count--;
        }
        super.update(newValue);
    }

    @Override
    protected void toggleLike(boolean isLike) {
        if (count == 0) {
            setText(R.string.label_like);
        } else {
            setText(String.valueOf(count));
        }
        super.toggleLike(isLike);
    }
}
