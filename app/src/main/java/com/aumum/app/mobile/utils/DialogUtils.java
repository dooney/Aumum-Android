package com.aumum.app.mobile.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Administrator on 17/10/2014.
 */
public class DialogUtils {
    public static void showDialog(Context context, CharSequence[] items,
                                  DialogInterface.OnClickListener itemsClickListener)
    {
        Dialog dlg = new AlertDialog.Builder(context)
                .setItems(items, itemsClickListener)
                .create();
        dlg.setCanceledOnTouchOutside(true);
        dlg.show();
    }

    public static void showDialog(Context context, int messageId,
                                  int positiveButtonTextId, DialogInterface.OnClickListener positiveListener,
                                  int negativeButtonTextId, DialogInterface.OnClickListener negativeListener) {
        Dialog dlg = new AlertDialog.Builder(context)
                .setPositiveButton(positiveButtonTextId, positiveListener)
                .setNegativeButton(negativeButtonTextId, negativeListener)
                .setMessage(messageId)
                .create();

        dlg.setCanceledOnTouchOutside(true);
        dlg.show();
    }
}
