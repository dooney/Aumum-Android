package com.aumum.app.mobile.ui.user;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.aumum.app.mobile.R;

public class UserTagListActivity extends ActionBarActivity {

    public static final String INTENT_USER_TAGS = "userTags";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tag_list);
    }
}
