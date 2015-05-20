package com.aumum.app.mobile.ui.group;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;

/**
 * Created by Administrator on 26/03/2015.
 */
public class GroupMemberPickerActivity extends BaseActionBarActivity {

    public static final String INTENT_SELECTED_MEMBERS = "userList";
    public static final String INTENT_ALL_MEMBERS = "allMembers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member_picker);
    }
}
