package com.aumum.app.mobile.ui.moment;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;

/**
 * Created by Administrator on 3/03/2015.
 */
public class MomentDetailsSingleActivity extends ProgressDialogActivity {

    public static final String INTENT_MOMENT_ID = "momentId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_details);

        progress.setMessageId(R.string.info_deleting_moment);
    }
}
