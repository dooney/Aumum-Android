package com.aumum.app.mobile.ui.view.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.aumum.app.mobile.R;

public class XFooterView extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_LOADING = 2;

    private View mLayout;

    private View mProgressBar;

    private int mState = STATE_NORMAL;

    public XFooterView(Context context) {
        super(context);
        initView(context);
    }

    public XFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mLayout = LayoutInflater.from(context).inflate(R.layout.vw_footer, null);
        mLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        addView(mLayout);

        mProgressBar = mLayout.findViewById(R.id.footer_progressbar);
    }

    /**
     * Set footer view state
     *
     * @see #STATE_LOADING
     * @see #STATE_NORMAL
     * @see #STATE_READY
     *
     * @param state
     */
    public void setState(int state) {
        if (state == mState) return;

        if (state == STATE_LOADING) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }

        mState = state;
    }

    /**
     * Set footer view bottom margin.
     *
     * @param margin
     */
    public void setBottomMargin(int margin) {
        if (margin < 0) return;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLayout.getLayoutParams();
        lp.bottomMargin = margin;
        mLayout.setLayoutParams(lp);
    }

    /**
     * Get footer view bottom margin.
     *
     * @return
     */
    public int getBottomMargin() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLayout.getLayoutParams();
        return lp.bottomMargin;
    }

    /**
     * hide footer when disable pull load more
     */
    public void hide() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLayout.getLayoutParams();
        lp.height = 0;
        mLayout.setLayoutParams(lp);
    }

    /**
     * show footer
     */
    public void show() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLayout.getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        mLayout.setLayoutParams(lp);
    }

}
