package com.aumum.app.mobile.ui.asking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 3/04/2015.
 */
public class AskingGroupListActivity extends ActionBarActivity {

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_BOARD_ID = "boardId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asking_group_list);

        final Intent intent = getIntent();
        setTitle(intent.getStringExtra(INTENT_TITLE));
    }
}
