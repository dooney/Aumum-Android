package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 31/12/2014.
 */
public class TextViewDialog extends ConfirmDialog {

    public TextViewDialog(Context context, String text, OnConfirmListener listener) {
        super(context, R.layout.dialog_text, listener);
        initView(text);
    }

    private void initView(String text) {
        TextView valueText = (TextView) findViewById(R.id.text_value);
        valueText.setText(Html.fromHtml(text));
    }

    @Override
    protected Object getValue() {
        return null;
    }
}
