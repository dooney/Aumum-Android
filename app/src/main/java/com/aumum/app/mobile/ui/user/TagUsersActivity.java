package com.aumum.app.mobile.ui.user;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 1/03/2015.
 */
public class TagUsersActivity extends ActionBarActivity {

    public static final String INTENT_USER_ID = "userId";
    public static final String INTENT_TAGS = "tags";
    public static final String INTENT_SHOULD_NOTIFY = "shouldNotify";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_users);
    }
}
