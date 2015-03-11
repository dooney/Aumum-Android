package com.aumum.app.mobile.ui.saving;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 12/03/2015.
 */
public class UserSavingsActivity extends ActionBarActivity {

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_savings);

        String title = getIntent().getStringExtra(INTENT_TITLE);
        setTitle(title);
    }
}
