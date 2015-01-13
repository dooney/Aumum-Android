package com.aumum.app.mobile.ui.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.view.Animation;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 13/01/2015.
 */
public class AboutAppActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ButterKnife.inject(this);

        Animation.flyIn(this);
    }
}
