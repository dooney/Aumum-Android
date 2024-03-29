package com.aumum.app.mobile.ui.vendor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 21/03/2015.
 */
public class EventListActivity extends ActionBarActivity {

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_CATEGORY = "category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_list);

        final Intent intent = getIntent();
        setTitle(intent.getStringExtra(INTENT_TITLE));
    }
}
