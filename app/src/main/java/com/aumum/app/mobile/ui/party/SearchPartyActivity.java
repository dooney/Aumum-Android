package com.aumum.app.mobile.ui.party;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

public class SearchPartyActivity extends ActionBarActivity {

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_NEARBY_PARTIES = "nearbyParties";
    public static final String INTENT_USER_ID = "userId";
    public static final String INTENT_IS_FAVORITE = "isFavorite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_party);

        String title = getIntent().getStringExtra(INTENT_TITLE);
        setTitle(title);
    }
}
