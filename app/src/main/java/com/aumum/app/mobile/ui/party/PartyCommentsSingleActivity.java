package com.aumum.app.mobile.ui.party;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.aumum.app.mobile.R;

public class PartyCommentsSingleActivity extends ActionBarActivity {

    public static final String INTENT_PARTY_ID = "partyId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_comments);
    }
}
