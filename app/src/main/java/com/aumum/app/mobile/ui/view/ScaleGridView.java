package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/**
 * Created by Administrator on 11/03/2015.
 */
public class ScaleGridView extends GridView {

    public ScaleGridView(Context context) {
        super(context);
    }

    public ScaleGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            heightSpec = View.MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        } else {
            heightSpec = heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
