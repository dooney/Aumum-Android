package com.aumum.app.mobile.ui.view.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.utils.EditTextUtils;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

/**
 * Created by Administrator on 31/12/2014.
 */
public class EditTextDialog extends ConfirmDialog {

    protected AutoCompleteTextView valueText;

    private final TextWatcher watcher = validationTextWatcher();

    public EditTextDialog(Context context, int layoutResId, int hintResId,
                          OnConfirmListener listener) {
        super(context, layoutResId, listener);
        initView(hintResId);
    }

    private void initView(int hintResId) {
        valueText = (AutoCompleteTextView) findViewById(R.id.et_value);
        valueText.setHint(hintResId);
        valueText.addTextChangedListener(watcher);
        valueText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && okButton.isEnabled()) {
                    okButton.performClick();
                    return true;
                }
                return false;
            }
        });
        valueText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(final TextView v, final int actionId,
                                          final KeyEvent event) {
                if (actionId == IME_ACTION_DONE && okButton.isEnabled()) {
                    okButton.performClick();
                    return true;
                }
                return false;
            }
        });
        EditTextUtils.showSoftInput(valueText, true);

        updateUIWithValidation();
    }

    @Override
    protected Object getValue() {
        return valueText.getText().toString();
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }
        };
    }

    private void updateUIWithValidation() {
        final boolean populated = populated(valueText);
        if (okButton != null) {
            okButton.setEnabled(populated);
        }
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    public void setText(String text) {
        valueText.setText(text);
        valueText.setSelection(valueText.getText().length());
    }
}