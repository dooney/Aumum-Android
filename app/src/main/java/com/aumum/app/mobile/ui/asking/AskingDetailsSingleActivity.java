package com.aumum.app.mobile.ui.asking;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;

/**
 * Created by Administrator on 23/02/2015.
 */
public class AskingDetailsSingleActivity extends ProgressDialogActivity {

    public static final String INTENT_ASKING_ID = "askingId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asking_details);

        progress.setMessageId(R.string.info_deleting_asking);
    }
}
