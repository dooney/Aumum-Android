package com.aumum.app.mobile.ui.moment;

import android.content.Intent;
import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;

/**
 * Created by Administrator on 19/06/2015.
 */
public class MomentGridActivity extends BaseActionBarActivity {

    public static String INTENT_QUERY = "query";
    public static final int QUERY_LATEST = 0;
    public static final int QUERY_HOTTEST = 1;
    public static final int QUERY_NEARBY = 2;
    public static final int QUERY_TALENT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_moment_grid);
        final Intent intent = getIntent();
        int query = intent.getIntExtra(INTENT_QUERY, 0);
        int titleId = R.string.label_latest;
        switch (query) {
            case QUERY_HOTTEST:
                titleId = R.string.label_hottest;
                break;
            case QUERY_NEARBY:
                titleId = R.string.label_nearby;
                break;
            case QUERY_TALENT:
                titleId = R.string.label_talent;
                break;
            default:
                break;
        }
        setTitle(getString(titleId));
    }
}
