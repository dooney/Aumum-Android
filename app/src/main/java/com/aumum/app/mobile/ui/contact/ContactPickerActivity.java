package com.aumum.app.mobile.ui.contact;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.aumum.app.mobile.R;

public class ContactPickerActivity extends ActionBarActivity {

    public static String INTENT_SELECTED_CONTACTS = "userList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);
    }
}
