package com.aumum.app.mobile.ui.recreation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.view.tab.TabPageIndicator;

/**
 * Created by Administrator on 26/03/2015.
 */
public class RecreationTabPageIndicator extends TabPageIndicator {

    public static final int TAB_CONVERSATION = 0;
    public static final int TAB_GROUP = 1;
    public static final int TAB_GAME = 2;

    public RecreationTabPageIndicator(Context context) {
        super(context, null);
    }

    public RecreationTabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected TabPageIndicator.TabView getTabView() {
        return new TabPageIndicator.TabView(getContext(), R.attr.topTabViewStyle);
    }

    @Override
    protected void drawTabView(ViewGroup tabView, CharSequence text, int iconResId) {
        TextView textView = new TextView(getContext(), null, R.attr.topTabTextStyle);
        textView.setText(text);
        tabView.addView(textView);
    }
}
