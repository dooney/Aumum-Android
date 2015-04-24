package com.aumum.app.mobile.ui.view.dialog;

/**
 * Created by Administrator on 17/10/2014.
 */
import android.content.Context;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.view.dialog.PopupDialog;

public class ProgressDialog extends PopupDialog {

    public void setMessageId(int messageId) {
        TextView infoText = (TextView) findViewById(R.id.text_info);
        infoText.setText(messageId);
    }

    public ProgressDialog(Context context) {
        super(context, R.layout.dialog_progress);
    }
}
