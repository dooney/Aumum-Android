package com.aumum.app.mobile.ui.user;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 15/01/2015.
 */
public class AreaUsersActivity extends ActionBarActivity {

    public static final String INTENT_AREA = "area";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_users);
    }
}
