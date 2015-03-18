package com.aumum.app.mobile.ui.discovery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.view.tab.TabPageIndicator;

/**
 * Created by Administrator on 16/03/2015.
 */
public class DiscoveryTabPageIndicator extends TabPageIndicator {

    public static final int TAB_CHANNEL = 0;
    public static final int TAB_ARTICLE = 1;
    public static final int TAB_MOMENT = 2;

    public DiscoveryTabPageIndicator(Context context) {
        super(context, null);
    }

    public DiscoveryTabPageIndicator(Context context, AttributeSet attrs) {
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
