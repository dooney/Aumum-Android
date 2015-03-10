package com.aumum.app.mobile.ui.special;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 10/03/2015.
 */
public class SpecialDetailsActivity extends ActionBarActivity {

    public final static String INTENT_SPECIAL_ID = "specialId";
    public final static String INTENT_SPECIAL_NAME = "specialName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_special_details);

        final Intent intent = getIntent();
        String name = intent.getStringExtra(INTENT_SPECIAL_NAME);
        String title = getString(R.string.title_activity_special_details, name);
        setTitle(title);
    }
}
