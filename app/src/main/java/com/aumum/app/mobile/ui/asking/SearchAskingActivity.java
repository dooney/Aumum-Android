package com.aumum.app.mobile.ui.asking;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

public class SearchAskingActivity extends ActionBarActivity {

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_asking);

        String title = getIntent().getStringExtra(INTENT_TITLE);
        setTitle(title);
    }
}
