package com.aumum.app.mobile.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 31/12/2014.
 */
public class EditTextDialog extends Dialog {

    private EditText valueText;
    private Button okButton;
    private ProgressBar progress;

    private OnConfirmListener listener;
    private final TextWatcher watcher = validationTextWatcher();

    public interface OnConfirmListener {
        public void call(String value) throws Exception;
        public void onException();
        public void onSuccess(String value);
    }

    public EditTextDialog(Context context, int hintResId, OnConfirmListener listener) {
        super(context);
        this.listener = listener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_text);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        initView(hintResId);
    }

    private void initView(int hintResId) {
        valueText = (EditText) findViewById(R.id.et_value);
        valueText.setHint(hintResId);
        valueText.addTextChangedListener(watcher);

        okButton = (Button) findViewById(R.id.b_dialog_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleProgress(true);
                final String value = valueText.getText().toString();
                new SafeAsyncTask<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        listener.call(value);
                        return true;
                    }

                    @Override
                    protected void onException(Exception e) throws RuntimeException {
                        if(!(e instanceof RetrofitError)) {
                            final Throwable cause = e.getCause() != null ? e.getCause() : e;
                            if(cause != null) {
                                Ln.e(e.getCause(), cause.getMessage());
                            }
                            listener.onException();
                        }
                        toggleProgress(false);
                    }

                    @Override
                    protected void onSuccess(Boolean success) throws Exception {
                        dismiss();
                        listener.onSuccess(value);
                    }
                }.execute();
            }
        });
        updateUIWithValidation();

        Button cancelButton = (Button) findViewById(R.id.b_dialog_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        progress = (ProgressBar) findViewById(R.id.progress);
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

    private void toggleProgress(boolean showProgress) {
        if (showProgress) {
            okButton.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
            okButton.setVisibility(View.VISIBLE);
        }
    }
}