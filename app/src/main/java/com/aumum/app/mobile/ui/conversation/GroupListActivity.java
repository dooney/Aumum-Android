package com.aumum.app.mobile.ui.conversation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 24/03/2015.
 */
public class GroupListActivity extends ActionBarActivity {

    public final static String INTENT_GROUP_LIST = "groupList";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
    }
}
