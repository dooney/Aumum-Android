package com.aumum.app.mobile.ui.view;

/**
 * Created by Administrator on 17/10/2014.
 */
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import com.aumum.app.mobile.R;

public class ProgressDialog extends Dialog {

    public void setMessageId(int messageId) {
        TextView infoText = (TextView) findViewById(R.id.text_info);
        infoText.setText(messageId);
    }

    public ProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}
