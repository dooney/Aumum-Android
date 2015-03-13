package com.aumum.app.mobile.ui.party;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.view.tab.TabPageIndicator;

/**
 * Created by Administrator on 13/03/2015.
 */
public class PartyTabPageIndicator extends TabPageIndicator {

    public PartyTabPageIndicator(Context context) {
        super(context, null);
    }

    public PartyTabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected TabView getTabView() {
        return new TabView(getContext(), R.attr.topTabViewStyle);
    }

    @Override
    protected void drawTabView(ViewGroup tabView, CharSequence text, int iconResId) {
        TextView textView = new TextView(getContext(), null, R.attr.topTabTextStyle);
        textView.setText(text);
        tabView.addView(textView);
    }
}
