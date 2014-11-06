package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import com.aumum.app.mobile.ui.moment.NewMomentActivity;

/**
 * Created by Administrator on 7/11/2014.
 */
public class CheckInTextView extends AnimateTextView {
    private Context context;

    public CheckInTextView(Context context) {
        super(context);
        this.context = context;
    }

    public CheckInTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CheckInTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        final Intent intent = new Intent(context, NewMomentActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void update(boolean newValue) {

    }
}
