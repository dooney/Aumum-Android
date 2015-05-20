package com.aumum.app.mobile.ui.contact;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;

public class ContactPickerActivity extends BaseActionBarActivity {

    public static String INTENT_SELECTED_CONTACTS = "userList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);
    }
}
