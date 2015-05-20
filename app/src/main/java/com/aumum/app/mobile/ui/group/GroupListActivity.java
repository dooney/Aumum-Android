package com.aumum.app.mobile.ui.group;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;

/**
 * Created by Administrator on 24/03/2015.
 */
public class GroupListActivity extends BaseActionBarActivity {

    public final static String INTENT_GROUP_LIST = "groupList";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
    }
}
