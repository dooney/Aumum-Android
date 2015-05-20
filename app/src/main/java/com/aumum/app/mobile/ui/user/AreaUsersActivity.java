package com.aumum.app.mobile.ui.user;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;

/**
 * Created by Administrator on 15/01/2015.
 */
public class AreaUsersActivity extends BaseActionBarActivity {

    public static final String INTENT_AREA = "area";
    public static final String INTENT_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_users);
    }
}
