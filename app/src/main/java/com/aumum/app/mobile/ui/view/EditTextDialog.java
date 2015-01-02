package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;

/**
 * Created by Administrator on 31/12/2014.
 */
public class EditTextDialog extends ConfirmDialog {

    private AutoCompleteTextView valueText;

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
        okButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    public AutoCompleteTextView getValueText() {
        return valueText;
    }
}