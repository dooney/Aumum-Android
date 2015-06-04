package com.aumum.app.mobile.ui.contact;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.ui.view.dialog.EditTextDialog;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;
import com.aumum.app.mobile.utils.EditTextUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 13/01/2015.
 */
public class SearchUserDialog extends EditTextDialog {

    public SearchUserDialog(Context context, OnConfirmListener listener) {
        super(context, R.layout.dialog_search_user, R.string.hint_search_user, listener);

        TextView selectCountryCodeText = (TextView) findViewById(R.id.text_select_country_code);
        selectCountryCodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCountryCodeListDialog();
            }
        });
    }

    private void showCountryCodeListDialog() {
        final ArrayList<String> countryCodes = new ArrayList<>(
                Constants.Map.COUNTRY.values());
        final ArrayList<String> countries = new ArrayList<>(
                Constants.Map.COUNTRY.keySet());
        new ListViewDialog(getContext(), null, countries,
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        valueText.setText(countryCodes.get(i));
                        EditTextUtils.showSoftInput(valueText, true);
                        valueText.setSelection(valueText.getText().length());
                    }
                }).show();
    }
}
