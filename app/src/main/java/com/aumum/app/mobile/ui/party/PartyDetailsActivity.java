package com.aumum.app.mobile.ui.party;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import com.aumum.app.mobile.R;

public class PartyDetailsActivity extends ActionBarActivity {

    public static final String INTENT_PARTY_ID = "partyId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_details);
    }
}
