package com.aumum.app.mobile.ui.moment;

import android.content.Intent;
import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;

/**
 * Created by Administrator on 3/03/2015.
 */
public class MomentDetailsActivity extends ProgressDialogActivity {

    private String momentId;

    public static final String INTENT_MOMENT_ID = "momentId";
    public static final String INTENT_DELETED = "deleted";
    public static final String INTENT_QUIT = "quit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_details);

        momentId = getIntent().getStringExtra(INTENT_MOMENT_ID);
        progress.setMessageId(R.string.info_deleting_moment);
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent();
        intent.putExtra(INTENT_MOMENT_ID, momentId);
        setResult(RESULT_OK, intent);
        finish();
    }
}
