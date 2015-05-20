package com.aumum.app.mobile.ui.chat;

import android.content.Intent;
import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;

public class ChatActivity extends BaseActionBarActivity {

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
    }
}
