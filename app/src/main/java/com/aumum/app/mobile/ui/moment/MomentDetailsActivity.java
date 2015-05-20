package com.aumum.app.mobile.ui.moment;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;

/**
 * Created by Administrator on 11/05/2015.
 */
public class MomentDetailsActivity extends BaseActionBarActivity {

    public static final String INTENT_MOMENT_ID = "momentId";
    public static final String INTENT_SHOW_INPUT = "showInput";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_moment_details);
    }
}
