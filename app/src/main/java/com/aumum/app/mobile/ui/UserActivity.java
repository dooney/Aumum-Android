package com.aumum.app.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.view.FollowTextView;

import butterknife.InjectView;
import butterknife.Views;

public class UserActivity extends ActionBarActivity {

    @InjectView(R.id.text_follow) protected FollowTextView followText;

    private String userId;

    public static final String INTENT_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        final Intent intent = getIntent();
        userId = intent.getStringExtra(INTENT_USER_ID);

        setContentView(R.layout.activity_user);

        Views.inject(this);

        followText.setFollowListener(new FollowListener(userId));
    }
}
