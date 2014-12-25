package com.aumum.app.mobile.ui.user;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.aumum.app.mobile.R;

public class UserListActivity extends ActionBarActivity {

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_USER_LIST = "userList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        final Intent intent = getIntent();
        setTitle(intent.getStringExtra(INTENT_TITLE));
    }
}
