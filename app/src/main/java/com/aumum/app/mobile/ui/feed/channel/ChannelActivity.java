package com.aumum.app.mobile.ui.feed.channel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 15/03/2015.
 */
public class ChannelActivity extends ActionBarActivity {

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_URI = "uri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        final Intent intent = getIntent();
        setTitle(intent.getStringExtra(INTENT_TITLE));
    }
}
