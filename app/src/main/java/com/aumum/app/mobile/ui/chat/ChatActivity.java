package com.aumum.app.mobile.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.view.Animation;

public class ChatActivity extends ActionBarActivity {

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_TYPE = "type";
    public static final String INTENT_ID = "id";

    public static final int TYPE_SINGLE = 1;
    public static final int TYPE_GROUP = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final Intent intent = getIntent();
        setTitle(intent.getStringExtra(INTENT_TITLE));

        Animation.scaleIn(this);
    }
}
