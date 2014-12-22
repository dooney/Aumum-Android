package com.aumum.app.mobile.ui.chat;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Window;

import com.aumum.app.mobile.R;

public class GroupDetailsActivity extends ActionBarActivity {

    public static final String INTENT_GROUP_ID = "groupId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
    }
}
