package com.aumum.app.mobile.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;

public class UIUtils {

    /**
     * Helps determine if the app is running in a Tablet context.
     *
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void showAlert(Context context, int titleId, CharSequence[] items,
                                 DialogInterface.OnClickListener itemsClickListener)
    {
        Dialog dlg = new AlertDialog.Builder(context)
                .setTitle(titleId)
                .setItems(items, itemsClickListener)
                .create();
        dlg.show();
    }
}
