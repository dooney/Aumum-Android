package com.aumum.app.mobile.ui.view.tab;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

/**
 * Created by Administrator on 8/01/2015.
 */
public class PopupDialog extends Dialog {

    public PopupDialog(Context context, int layoutResId) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(layoutResId);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}
