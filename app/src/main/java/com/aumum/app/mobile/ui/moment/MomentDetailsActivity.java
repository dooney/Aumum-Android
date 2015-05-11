package com.aumum.app.mobile.ui.moment;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 11/05/2015.
 */
public class MomentDetailsActivity extends ActionBarActivity {

    public static final String INTENT_MOMENT_ID = "momentId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_moment_details);
    }
}
