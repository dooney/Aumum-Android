package com.aumum.app.mobile.ui.asking;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;

public class AskingDetailsActivity extends ProgressDialogActivity {

    public static String INTENT_ASKING_ID = "askingId";
    public static String INTENT_TITLE = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getStringExtra(INTENT_TITLE));
        setContentView(R.layout.activity_asking_details);

        progress.setMessageId(R.string.info_deleting_asking);
    }
}
