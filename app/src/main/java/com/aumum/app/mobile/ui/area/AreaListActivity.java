package com.aumum.app.mobile.ui.area;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 14/01/2015.
 */
public class AreaListActivity extends ActionBarActivity {

    public static final String INTENT_CITY = "city";
    public static final String INTENT_AREA = "area";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_list);
    }
}
