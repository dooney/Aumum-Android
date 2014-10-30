package com.aumum.app.mobile.ui.message;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

public class MessageListActivity extends ActionBarActivity {

    public static final String INTENT_MESSAGE_TYPE = "messageType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message);
    }
}
