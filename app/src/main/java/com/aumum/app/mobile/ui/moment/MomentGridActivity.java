package com.aumum.app.mobile.ui.moment;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;

/**
 * Created by Administrator on 19/06/2015.
 */
public class MomentGridActivity extends BaseActionBarActivity {

    public static String INTENT_QUERY = "query";
    public static int QUERY_LATEST = 0;
    public static int QUERY_HOTTEST = 1;
    public static int QUERY_NEARBY = 2;
    public static int QUERY_TALENT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_moment_grid);
    }
}
