package com.aumum.app.mobile.ui.account;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 15/12/2014.
 */
public class ConfirmPhoneDialog extends Dialog {

    private OnConfirmListener listener;

    interface OnConfirmListener {
        public void onConfirmPhone();
    }

    public ConfirmPhoneDialog(Context context, OnConfirmListener listener) {
        super(context);
        this.listener = listener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_phone);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        initView(context);
    }

    private void initView(Context context) {
        TextView textView = (TextView) findViewById(R.id.text_will_send_verification_code);
        textView.setText(Html.fromHtml(context.getString(R.string.label_will_send_verification_code)));
        View okButton = findViewById(R.id.b_dialog_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onConfirmPhone();
                }
                dismiss();
            }
        });
        Button cancelButton = (Button) findViewById(R.id.b_dialog_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setPhone(String countryCode, String phone) {
        String text = countryCode + " " + phone;
        TextView phoneText = (TextView) findViewById(R.id.text_phone);
        phoneText.setText(text);
    }
}
