package com.aumum.app.mobile.ui.chat;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 26/03/2015.
 */
public class GroupMemberPickerActivity extends ActionBarActivity {

    public static final String INTENT_SELECTED_MEMBERS = "userList";
    public static final String INTENT_ALL_MEMBERS = "allMembers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member_picker);
    }
}
