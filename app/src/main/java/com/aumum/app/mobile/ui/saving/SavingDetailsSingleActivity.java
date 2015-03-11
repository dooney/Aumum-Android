package com.aumum.app.mobile.ui.saving;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;

/**
 * Created by Administrator on 12/03/2015.
 */
public class SavingDetailsSingleActivity extends ProgressDialogActivity {

    public static final String INTENT_SAVING_ID = "savingId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_details);

        progress.setMessageId(R.string.info_deleting_saving);
    }
}
