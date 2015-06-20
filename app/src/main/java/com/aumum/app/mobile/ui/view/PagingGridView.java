package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 20/06/2015.
 */
public class PagingGridView extends com.paging.gridview.PagingGridView {

    public PagingGridView(Context context) {
        super(context);
    }

    public PagingGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PagingGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void addFooterView(View v) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
