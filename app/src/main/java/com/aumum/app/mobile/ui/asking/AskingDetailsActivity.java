package com.aumum.app.mobile.ui.asking;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

public class AskingDetailsActivity extends ActionBarActivity {

    public static String INTENT_ASKING_ID = "askingId";
    public static String INTENT_TITLE = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getStringExtra(INTENT_TITLE));
        setContentView(R.layout.activity_asking_details);
    }
}
