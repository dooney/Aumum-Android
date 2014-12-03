package com.aumum.app.mobile.ui.party;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

public class PartyCommentsActivity extends ActionBarActivity {

    private String partyId;

    public static final String INTENT_PARTY_ID = "partyId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_comments);

        partyId = getIntent().getStringExtra(INTENT_PARTY_ID);
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent();
        intent.putExtra(INTENT_PARTY_ID, partyId);
        setResult(RESULT_OK, intent);
        finish();
    }
}
