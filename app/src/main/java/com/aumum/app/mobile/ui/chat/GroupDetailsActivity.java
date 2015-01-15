package com.aumum.app.mobile.ui.chat;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;

public class GroupDetailsActivity extends ProgressDialogActivity {

    public static final String INTENT_GROUP_ID = "groupId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
    }
}
