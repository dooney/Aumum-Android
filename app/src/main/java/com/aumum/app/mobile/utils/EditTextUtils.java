package com.aumum.app.mobile.utils;

/**
 * Created by Administrator on 12/10/2014.
 */
import android.content.Context;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * EditText utils
 */
public class EditTextUtils {
    private EditTextUtils() {
        throw new AssertionError();
    }

    /**
     * returns text string from passed EditText
     */
    public static String getText(EditText edit) {
        if (edit.getText() == null) {
            return "";
        }
        return edit.getText().toString();
    }

    /**
     * moves caret to end of text
     */
    public static void moveToEnd(EditText edit) {
        if (edit.getText() == null) {
            return;
        }
        edit.setSelection(edit.getText().toString().length());
    }

    /**
     * returns true if nothing has been entered into passed editor
     */
    public static boolean isEmpty(EditText edit) {
        return TextUtils.isEmpty(getText(edit));
    }

    /**
     * hide the soft keyboard for the passed EditText
     */
    public static void hideSoftInput(EditText edit) {
        if (edit == null) {
            return;
        }

        InputMethodManager imm = getInputMethodManager(edit);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
        }
    }

    /**
     * show the soft keyboard for the passed EditText
     */
    public static void showSoftInput(final EditText edit, boolean requestFocus) {
        if (edit == null) {
            return;
        }

        if (requestFocus) {
            edit.requestFocus();
        }

        final InputMethodManager imm = getInputMethodManager(edit);
        if (imm != null) {
            edit.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imm.showSoftInput(edit, 0);
                }
            }, 100);
        }
    }

    private static InputMethodManager getInputMethodManager(EditText edit) {
        Context context = edit.getContext();
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }
}
