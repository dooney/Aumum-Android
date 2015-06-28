package com.aumum.app.mobile.ui.user;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;

/**
 * Created by Administrator on 28/06/2015.
 */
public class UserContactsActivity extends BaseActionBarActivity {

    public static String INTENT_TITLE = "title";
    public static String INTENT_CONTACTS = "contacts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_contacts);

        setTitle(getIntent().getStringExtra(INTENT_TITLE));
    }
}
