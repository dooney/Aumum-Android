package com.aumum.app.mobile.ui.view;

/**
 * Created by Administrator on 17/10/2014.
 */
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;

public class ProgressDialog extends DialogFragment {

    private int messageId;

    public static ProgressDialog newInstance(int messageId) {
        return new ProgressDialog(messageId);
    }

    private ProgressDialog(int messageId) {
        this.messageId = messageId;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.app.ProgressDialog dialog = new android.app.ProgressDialog(getActivity());
        dialog.setMessage(getString(messageId));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        };
        dialog.setOnKeyListener(keyListener);

        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("das", messageId);
    }
}
