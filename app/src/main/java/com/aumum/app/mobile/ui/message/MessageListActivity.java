package com.aumum.app.mobile.ui.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

public class MessageListActivity extends ActionBarActivity {

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_SUBCATEGORY = "subCategory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message);

        final Intent intent = getIntent();
        String title = getString(intent.getIntExtra(INTENT_TITLE, R.string.app_name));
        setTitle(title);
    }
}
