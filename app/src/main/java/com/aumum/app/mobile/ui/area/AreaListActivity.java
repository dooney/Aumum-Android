package com.aumum.app.mobile.ui.area;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;

/**
 * Created by Administrator on 14/01/2015.
 */
public class AreaListActivity extends BaseActionBarActivity {

    public static final String INTENT_CITY = "city";
    public static final String INTENT_AREA = "area";
    public static final String INTENT_TITLE = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_list);

        final String title = getIntent().getStringExtra(INTENT_TITLE);
        if (title != null) {
            setTitle(title);
        }
    }
}
