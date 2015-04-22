package com.aumum.app.mobile.ui.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.view.tab.TabPageIndicator;

import java.util.ArrayList;

/**
 * Created by Administrator on 29/12/2014.
 */
public class MainTabPageIndicator extends TabPageIndicator {

    public static final int TAB_CHAT = 0;
    public static final int TAB_CONTACT = 1;
    public static final int TAB_PROFILE = 2;

    private ArrayList<ImageView> unreadImages = new ArrayList<ImageView>();

    public MainTabPageIndicator(Context context) {
        super(context, null);
    }

    public MainTabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected TabView getTabView() {
        return new TabView(getContext(), R.attr.mainTabViewStyle);
    }

    @Override
    protected void drawTabView(ViewGroup tabView, CharSequence text, int iconResId) {
        TextView textView = new TextView(getContext(), null, R.attr.mainTabTextStyle);
        textView.setText(text);
        if (iconResId != 0) {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, iconResId, 0, 0);
        }
        tabView.addView(textView);

        ImageView unreadImage = new ImageView(getContext(), null, R.attr.tabImageStyle);
        unreadImage.setImageDrawable(getResources().getDrawable(R.drawable.image_unread_background));
        unreadImage.setVisibility(INVISIBLE);
        tabView.addView(unreadImage);
        unreadImages.add(unreadImage);
    }

    public ImageView getUnreadImage(int index) {
        return unreadImages.get(index);
    }
}
