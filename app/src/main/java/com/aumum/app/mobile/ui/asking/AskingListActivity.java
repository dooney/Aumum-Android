package com.aumum.app.mobile.ui.asking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 31/03/2015.
 */
public class AskingListActivity extends ActionBarActivity {

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_GROUP_ID = "category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_asking_list);
    }
}
