package com.aumum.app.mobile.ui.register;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.Window;
import android.widget.TextView;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 15/12/2014.
 */
public class ConfirmPhoneDialog extends Dialog {

    public ConfirmPhoneDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_phone);
        TextView textView = (TextView) findViewById(R.id.text_will_send_verification_code);
        textView.setText(Html.fromHtml(context.getString(R.string.label_will_send_verification_code)));
    }

    public void setPhone(String phone) {
        TextView textView = (TextView) findViewById(R.id.text_phone);
        textView.setText(phone);
    }
}
