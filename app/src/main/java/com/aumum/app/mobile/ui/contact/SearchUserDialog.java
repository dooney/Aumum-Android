package com.aumum.app.mobile.ui.contact;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.ui.view.dialog.EditTextDialog;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;
import com.aumum.app.mobile.utils.EditTextUtils;

import java.util.Arrays;

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
        final String countryCodes[] = Constants.Options.COUNTRY_CODES;
        String options[] = getContext().getResources().getStringArray(R.array.label_country_code_list);
        new ListViewDialog(getContext(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        valueText.setText(countryCodes[i]);
                        EditTextUtils.showSoftInput(valueText, true);
                        valueText.setSelection(valueText.getText().length());
                    }
                }).show();
    }
}
