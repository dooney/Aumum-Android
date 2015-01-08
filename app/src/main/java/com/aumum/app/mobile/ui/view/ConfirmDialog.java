package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.view.tab.PopupDialog;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 31/12/2014.
 */
public class ConfirmDialog extends PopupDialog {

    protected Button okButton;
    protected Button cancelButton;
    protected ProgressBar progress;
    private OnConfirmListener listener;

    public interface OnConfirmListener {
        public void call(Object value) throws Exception;
        public void onException(String errorMessage);
        public void onSuccess(Object value);
    }

    public ConfirmDialog(Context context, int layoutResId, OnConfirmListener listener) {
        super(context, layoutResId);
        this.listener = listener;
        initView();
    }

    protected void initView() {
        okButton = (Button) findViewById(R.id.b_dialog_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleProgress(true);
                final Object value = getValue();
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
                                listener.onException(cause.getMessage());
                            }
                        }
                    }

                    @Override
                    protected void onSuccess(Boolean success) throws Exception {
                        dismiss();
                        listener.onSuccess(value);
                    }

                    @Override
                    protected void onFinally() throws RuntimeException {
                        toggleProgress(false);
                    }
                }.execute();
            }
        });

        cancelButton = (Button) findViewById(R.id.b_dialog_cancel);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }

        progress = (ProgressBar) findViewById(R.id.progress);
    }

    private void toggleProgress(boolean showProgress) {
        if (showProgress) {
            okButton.setVisibility(View.GONE);
            if (cancelButton != null) {
                cancelButton.setEnabled(false);
            }
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
            okButton.setVisibility(View.VISIBLE);
            if (cancelButton != null) {
                cancelButton.setEnabled(true);
            }
        }
    }

    protected Object getValue() {
        return null;
    }
}
