package com.aumum.app.mobile.ui.saving;

import android.content.Intent;
import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;

/**
 * Created by Administrator on 12/03/2015.
 */
public class SavingDetailsActivity extends ProgressDialogActivity {

    private String savingId;

    public static final String INTENT_SAVING_ID = "savingId";
    public static final String INTENT_DELETED = "deleted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_details);

        savingId = getIntent().getStringExtra(INTENT_SAVING_ID);
        progress.setMessageId(R.string.info_deleting_saving);
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent();
        intent.putExtra(INTENT_SAVING_ID, savingId);
        setResult(RESULT_OK, intent);
        finish();
    }
}
